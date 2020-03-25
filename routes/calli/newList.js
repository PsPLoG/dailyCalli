const express = require('express');
const router = express.Router();
const async = require('async');
const pool = require('../../config/dbPool');
const crypto=require('crypto');
const moment =require('moment');
const verify = require('../jwt_verify');

//게시글 상세보기---------/calli/newList/-------------
router.get('/', (req, res) => {
  let com=[];
	let taskArray = [
    (callback) => {
      let verify_data = verify(req.headers.user_token);
      callback(null, verify_data);
    },

		//1. connection을 pool로부터 가져옴
		(verify_data, callback) => {
			pool.getConnection((err, connection) => {
				if(err){
					res.status(500).send({
						msg : "fail"
					});
					callback("fail reason : " + err);
				} else callback(null, verify_data, connection);
			});
		},
		//2. 게시글 상세보기
		(verify_data, connection, callback) => {
      let com_count=0;
      let scrap_count=0;
      let calli_id=Number(req.params.calli_id);
      let selectAtdQuery = "select cal.*, us.user_nickname, us.user_img, if(isnull(com.commentCount),0,com.commentCount) as commentCount, "+
      "if(isnull(li.likeCount),0,li.likeCount) as likeCount, if(isnull(lb.likeBool),false, true) as likeBool from calli.calli as cal "+
      "left outer join users as us on cal.user_id = us.user_id "+
      "left outer join (select count(*) as commentCount, calli_id from calli.comment group by calli_id) as com on cal.calli_id = com.calli_id "+
      "left outer join (select count(*) as likeCount, calli_id from calli.calliLike group by calli_id) as li on cal.calli_id = li.calli_id "+
      "left outer join (select user_id as likeBool, calli_id from calli.calliLike where user_id = ?) as lb on cal.calli_id = lb.calli_id "+
      "order by cal.calli_date desc";
			connection.query(selectAtdQuery, verify_data.user_id, (err, data) => {
				if(err){
					res.status(500).send({
						msg : "fail"
					});
					connection.release();
					callback("fail reason: "+ err);
				} else{

            var calliBool;
            var likeBool;
            var pack = [];
            var i;
            for( i =0; i< data.length; i++){
              if(data[i].likeBool===1){
                likeBool = true;
              }else{
                likeBool = false;
              }
              pack[i] = {
                calli_id: data[i].calli_id,
                calli_img: data[i].calli_img,
                calli_txt: data[i].calli_txt,
                calli_date: moment(data[i].calli_date).format('YYYY.MM.DD'),
                calli_tag: data[i].calli_tag,
                user_nickname: data[i].user_nickname,
                user_img: data[i].user_img,
                commentCount: data[i].commentCount,
                likeCount: data[i].likeCount,
                likeBool: likeBool
              };
            }
            res.status(200).send({
              msg:"success",
              calliList : pack
            });
            connection.release();
            callback(null,"success");
				}
			});

		}

	];
	async.waterfall(taskArray , (err, result)=> {
		if(err) console.log(err);
		else console.log(result);
	});
});

module.exports = router;
