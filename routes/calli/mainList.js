const express = require('express');
const router = express.Router();
const async = require('async');
const pool = require('../../config/dbPool');
const crypto=require('crypto');
const verify = require('../jwt_verify');


//메인리스트----------localhost:3000/calli/mainList-------------
router.get('/', (req, res) => {
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
					callback("connection err : " + err);
				} else callback(null, verify_data, connection);
			});

		},

		//2. 전체 리스트 출력

		(verify_data, connection, callback) => {
      let com_count=0;
      let scrap_count=0;
			let selectAtdQuery = "select cl.*, us.user_nickname, us.user_id, us.user_img From calli.calli as cl "+
"left outer join calli.users as us on cl.user_id = us.user_id "+
"where calli_id in (select calli_id from calli.calliLike group by calli_id having count(*) >= 1) "+
"order by rand()";
			connection.query(selectAtdQuery,  (err, data) => {
        console.log("verify_data.user_id: "+verify_data.user_id);
				if(err){
					res.status(500).send({
						msg : "fail"
					});
					connection.release();
					callback("fail reason: "+ err);
				} else{
					var i,j=0;
					var pack=[];
					for(i=0;i<data.length;i++){

							pack[j]={
								user_id : data[i].user_id,
								user_nickname : data[i].user_nickname,
								user_img: data[i].user_img,
								calli_img: data[i].calli_img,
								calli_txt: data[i].calli_txt,
								calli_id: data[i].calli_id
							};

							j=j+1;
					}
          res.status(200).send({
            msg: "success",
            calliList:pack
          });
					connection.release();
						callback(null,"mainList success");
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
