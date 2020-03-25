var express = require('express');
var router = express.Router();
var profileImg = require('./profileImg');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.use('/profileImg', profileImg);
module.exports = router;
