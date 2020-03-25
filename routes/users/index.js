var express = require('express');
var router = express.Router();
var signup = require('./signup');
var login = require('./login');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.use('/signup', signup);
router.use('/login', login);
module.exports = router;
