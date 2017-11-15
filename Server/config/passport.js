var JwtStrategy = require('passport-jwt').Strategy;
var ExtractJwt = require('passport-jwt').ExtractJwt;
var User = require('../api/models/user');
var config = require('./database');

module.exports = function(passport) {
    var options = {}
    options.secretOrKey = config.secret;
    options.jwtFromRequest = ExtractJwt.fromAuthHeaderAsBearerToken();

    passport.use(new JwtStrategy(options, function(jwt_payload, done){
        User.find({id: jwt_payload.id}, function(err, user) {
            if(err){
                return done(err, false);
            }
            if(user){
                done(null, user);
            } else {
                done(null, false);
            }
        });
    }));
};