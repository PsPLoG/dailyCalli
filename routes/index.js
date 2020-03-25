var express = require('express');
var router = express.Router();
var users = require('./users/index');
var calli = require('./calli/index');
var myPage = require('./myPage/index');
var comment = require('./comment/index');
var tokenCheck = require('./tokenCheck');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.use('/users', users);
router.use('/calli', calli);
router.use('/myPage', myPage);
router.use('/comment', comment);
router.use('/tokenCheck', tokenCheck);
module.exports = router;
