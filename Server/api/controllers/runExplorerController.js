'use strict';

var mongoose = require('mongoose'),
    Route = mongoose.model('Routes'),
    User = mongoose.model('Users'),
    jwt = require('jwt-simple'),
    config = require('../../config/database'),
    generateId = mongoose.Types.ObjectId;

exports.signUp = function(req, res) {
    if(!req.body.username || !req.body.password){
        return res.json({success: false, msg: 'Please pass name and password.'});
    } else {
        var newUser =  new User({
            username: req.body.username,
            password: req.body.password
        }).save(function(err){
            if(err){
                return res.json({success: false, msg: 'Username already exists'});
            } else {
                return res.json({success: true, msg: 'Successfully created'});
            }
        })
    }
}

exports.authenticate = function(req, res) {
    User.findOne({
        name: req.body.name
    }, function(err, user) {
        if(err) throw err

        if(!user){
            return res.status(403).json({success: false, msg: 'Authentication failed. User not found'});
        } else {
            user.comparePassword(req.body.password, function(err, isMatch){
                if(isMatch && !err){
                    var token = jwt.encode({name: user.username}, config.secret);
                    return res.json({success: true, token: 'JWT ' + token});
                } else {
                    return res.status(403).json({success: false, msg: 'Authentication failed. Wrong password'});
                }
            })
        }
    })
}

exports.addRoute = function(req, res) {
    var token = getToken(req.headers);
    if(token) {
        var decoded = jwt.decode(token, config.secret);
        User.findOne({
            name: decoded.username
        }, function(err, user){
            if(err) throw err;

            if(!user){
                return res.status(403).json({success: false, msg: 'Authentication failed, no user found'});
            } else {
                var id = generateId();
                var newRoute = new Route({
                    routeId: id,
                    points: req.body.points,
                    bestTime: req.body.time,
                    bestUser: user.username,
                    distance: req.body.distance,
                });
                newRoute.save(function(err){
                    if(err){
                        return res.json({success: false, msg: 'Cant save route ' + err});
                    } else {
                        user.usersRoutes.push({
                            routeId: id, 
                            timesPer100: req.body.times
                        });
                        return res.json({success: true, msg: 'Route added'});
                    }
                });
            }
        });
    } else {
        return res.status(403).json({success: false, msg: 'Authentication failed. Wrong password'});
    }
}

exports.getAllRoutes = function(req, res) {
    Route.find({}, function(err, routes) {
        if(err)
            throw err;
        if(routes.length === 0)
            return res.json({success: false, msg: 'No routes found'});
        else{
            return res.json({routes: routes});
        }
    })
}

function getToken(headers){
    if(headers && headers.authorization) {
        var parted = headers.authorization.split(' ');
        if(parted.length === 2){
            return parted[1];
        } else {
            return null;
        }
    } else {
        return null;
    }
}

