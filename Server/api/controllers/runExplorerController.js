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
        username: req.body.username
    }, function(err, user) {
        if(err)
            res.status(500).json({success: false, msg: 'Server error'});
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
            if(err)
                res.status(500).json({success: false, msg: 'Server error'});
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

exports.getRoute = function(req, res) {
    if(req.query.routeId === undefined)
        return res.json({success: false, msg: 'No routes found'});
    Route.findOne({routeId: req.query.routeId}, function(err, route) {
        if(err)
            throw err;
        if(route === undefined)
            return res.json({success: false, msg: 'No routes found'});
        else{
            return res.json({success: true, route: route});
        }
    })
}

exports.getAllUsers = function(req, res) {
    User.find({}, function(err, users){
        if(err)
            res.status(500).json({success: false, msg: 'Server error'});
        if(users.length === 0)
            return res.json({success: false, msg: 'No users found'});
        else
            return res.json({success: true, users: users});
    })
}

exports.getUsersRoutes = function(req, res) {
    User.findOne({username: req.params.username}, function(err, user){
        if(err)
            return res.json({success: false, msg: 'No user found'});
        let skipNumber = req.query.skip;
        if(skipNumber === undefined)
            skipNumber = 0;
        const userRoutesData = user.usersRoutes.map(r => ({
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

exports.postAnotherRun = function(req, res) {
    User.findOne({username: req.params.username}, function(err, user){
        if(err)
            return res.json({success: false, msg: 'No user found'});
        user.usersRoutes.push({
            routeId: req.body.routeId,
            timesPer100: req.body.timesPer100,
            date: req.body.date,
            time: req.body.time,
        });
        user.save(function(err){
            if(err)
                return res.json({success: false, msg: 'Cant add new run'});
            Route.findOne({routeId: req.body.routeId}, function(err, route){
                if(err)
                    return res.json({success: false, msg: 'Cant find given route'});
                if(route.bestTime > req.body.time){
                    route.bestTime = req.body.time;
                    route.bestUser = req.params.username;
                    route.save(function(err){
                        if(err){
                            return res.json({success: false, msg: 'Cant update route'});
                        return res.json({success: true, msg: 'New routes record!'});
                        }
                    })
                }
                return res.json({success: true, msg: 'New run added'});
            })
        })
    })
}

exports.search = function(req, res) {
    const searchCryteria = {
        searchedDistance: {max: parseInt(req.query.maxDistance), min: parseInt(req.query.minDistance)},
        searchedUser: {username: req.query.username},
        searchedCircle: {lat: parseFloat(req.query.lat), lng: parseFloat(req.query.lng), radius: parseFloat(req.query.radius)},
    }

    const searchMechanisms = {
        searchedDistance : function(routes, distance) {
            return new Promise((resolve, reject) => {
                if(routes.length !== 0)
                    resolve(
                        routes.filter(r => 
                            r.distance < distance.max && r.distance > distance.min )
                    );
                else
                    reject([])
            })
        },
        searchedUser: function(routes, user){
            return new Promise((resolve, reject) => {
                if(routes.length === 0)
                    reject([])
                else{
                    User.findOne({username: user.username}, function(err, user){
                        if(err)
                            reject([])
                        const routeIds = user.usersRoutes.map(r => r.routeId);
                        resolve(routes.filter(r => routeIds.indexOf(r) > -1));
                    })
                }
            })
            
        },
        searchedCircle: function(routes, searchedCircle){
            return new Promise((resolve, reject) => {
                if(routes.length !== 0){
                    resolve(
                        routes.filter(r => {
                        const start = r.points[0];
                        return (start.lat - searchedCircle.lat)*(start.lat - searchedCircle.lat)
                            + (start.lng - searchedCircle.lng)*(start.lng - searchedCircle.lng) 
                            < searchedCircle.radius*searchedCircle.radius; 
                    }));
                }else{
                    reject([]);
                }
            })
        }
    }

    Route.find({}, (err, routes) => {
        if(err)
            throw err;
        Object.keys(searchCryteria).forEach(k => {
            filterWith(searchCryteria[k], searchMechanisms[k], routes)
                .then((filteredResult) => {
                    routes = routes.filter(r => filteredResult.indexOf(r) > -1);
                })
        })
        const results = routes.map(r => 
        ({
            id: r.routeId,
            time: r.bestTime,
            distance: r.distance,
        }))
        return res.json({success: true, routes: results});
    })
}

async function filterWith(searchCryterium, searchMechanism, routes){
    let result = [];
    if(isDefined(searchCryterium))
        result = await searchMechanism(routes, searchCryterium);
    return result;
}

function isDefined(obj){
    return Object.keys(obj).reduce((isDefined, key) => {
        return isDefined && obj[key] !== undefined;
    }, true);
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

