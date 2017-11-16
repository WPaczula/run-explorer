'use strict';
var passport = require('passport');

module.exports = function(app) {
  var controller = require('../controllers/runExplorerController');

  // todoList Routes
  app.route('/Routes', passport.authenticate('jwt', {session: false}))
    .post(controller.addRoute)
    .get(controller.getAllRoutes);

  app.route('/Users')
    .get(controller.getAllUsers);

  app.route('/Routes/:username', passport.authenticate('jwt', {session: false}))
    .get(controller.getUsersRoutes)
    .post(controller.postAnotherRun);

  app.route('/SignUp')
    .post(controller.signUp);

  app.route('/Authenticate')
    .post(controller.authenticate);

  app.route('/')
    .get(function(req, res){
        res.json({success: true, msg: 'dummy route'});
    });
};
