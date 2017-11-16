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
                        return res.json({success: false, msg: 'Cant save route '});
                    } else {
                        user.usersRoutes.push({
                            routeId: id, 
                            timesPer100: req.body.times,
                            date: req.body.date,
                            time: req.body.time,
                        });
                        user.save(function(err){
                            if(err)
                                return res.json({success: false, msg: 'Cant update user data' + err});
                            return res.json({success: true, msg: 'Route added'});
                        })
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
            return res.json({success: false, routes: routes});
        }
    })
}

exports.getAllUsers = function(req, res) {
    User.find({}, function(err, users){
        if(err)
            throw err;
        if(users.length === 0)
            return res.json({success: false, msg: 'No users found'});
        else
            return res.json({success: true, users: users});
    })
}

exports.getUsersRoutes = function(req, res) {
    User.find({username: req.params.username}, function(err, user){
        if(err)
            return res.json({success: false, msg: 'No user found'});
        const skipNumber = req.query.skip;
        const userRoutesData = user[0].usersRoutes.map(r => ({
            routeId: r.routeId, 
            date: r.date,
            time: r.time,
        }));
        Route.find({routeId: { $in:
            userRoutesData.map(r => r.routeId)
            }
        }, 
        function(err, routes){
            if(err)
                return res.json({success: false, msg: 'Can not find routes'});
            const routesData = [];
            if(routes.length === 0)
                return res.json({success: true, routes: []});
            userRoutesData.forEach(usersRoute => {
                const foundRoute = routes.find(function(route) {return route.routeId === usersRoute.routeId});
                routesData.push({
                    id: foundRoute.routeId,
                    date: usersRoute.date,
                    time: usersRoute.time,
                    distance: foundRoute.distance,
                })
            })
            return res.json({success: true, routes: routesData});
        })
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

