'use strict';
var passport = require('passport');

module.exports = function(app) {
  var controller = require('../controllers/runExplorerController');

  // todoList Routes
  app.route('/UserRoutes/:userName')
    .post(passport.authenticate('jwt', {session: false}), controller.addRoute);

  app.route('/SignUp')
    .post(controller.signUp)

  app.route('/Authenticate')
    .post(controller.authenticate)

  app.route('/')
    .get(function(req, res){
        res.json({success: true, msg: 'dummy route'});
    })
};