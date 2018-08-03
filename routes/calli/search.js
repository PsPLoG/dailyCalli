const express = require('express');
const router = express.Router();
const async = require('async');
const pool = require('../../config/dbPool');
const crypto=require('crypto');
const verify = require('../jwt_verify');
const formData = require('form-data');

//검색----------localhost:3000/calli/search-------------
router.post('/', (req, res) => {
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
					callback("fail : " + err);
				} else callback(null, verify_data, connection);
			});
		},
		//2. 말머리별 게시글 검색
		(verify_data, connection, callback) => {
      var queryS = 'select c.* from calli.calli c where match(calli_tag) against(? in boolean mode) ';
      var keyS;
      connection.query(queryS,keyS, (err, data)=>{
        if(err){
          res.status(500).send({
            "msg": "fail"
          })
          connection.release();
          callback("err " +err);
        }else{
          
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
