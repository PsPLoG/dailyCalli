const express = require('express');
const router = express.Router();
const async = require('async');
const pool = require('../../config/dbPool');
const crypto=require('crypto');
const multer = require('multer');
const multerS3 = require('multer-s3');
const aws = require('aws-sdk');
aws.config.loadFromPath('./config/aws_config.json');
const verify = require('../jwt_verify');

const s3 = new aws.S3();
const upload = multer({
    storage: multerS3({
        s3: s3,
        bucket: 'jiyoon1217',
        acl: 'public-read',
        key: function(req, file, cb) {
            cb(null, Date.now() + '.' + file.originalname.split('.').pop());
        }
    })
});

//캘리업로드----------localhost:3000/calli/upload-------------
router.post('/',upload.array('image',5), (req, res) => {
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
		//2. 전체 리스트 출력
		(verify_data, connection, callback) => {
      let calli_trace= Number(req.body.calli_trace);
      let guide_id;
      let calli_txt;
      let calli_tag= req.body.calli_tag;
      let user_id=Number(verify_data.user_id);
      let image=new Array();

      if(req.body.calli_txt === undefined){
        calli_txt = null;
      }else{
         calli_txt =req.body.calli_txt;
      }
      if(calli_trace === 1){
        guide_id = req.body.guide_id;
      }else{
        guide_id = null;
      }

      for(let i=0; i<req.files.length; ++i){
        image[i]=req.files[i].location;
      }
      let calli_img=  JSON.stringify(image);

			let selectAtdQuery = 'insert into calli(user_id, calli_tag, calli_txt, calli_trace, guide_id, calli_img) values(?, ?, ?, ?, ?, ?)';
			connection.query(selectAtdQuery,[ user_id, calli_tag, calli_txt, calli_trace, guide_id, calli_img] , (err, data) => {
				if(err){
					res.status(500).send({
						msg : "fail"
					});
					connection.release();
					callback("fail reason: "+ err);
				} else{
          res.status(200).send({
						msg:"success",
					});
          connection.release();
          callback(null, "successful calli write");
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
