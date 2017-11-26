'use strict';

var mongoose = require('mongoose'),
    Route = mongoose.model('Routes'),
    User = mongoose.model('Users'),
    jwt = require('jwt-simple'),
    config = require('../../config/database'),
    generateId = mongoose.Types.ObjectId;

const max = 5;    

exports.changeName = function(req, res) {
    if(!req.body.date || !req.body.newName){
        return res.status(404).json({success: false, message: 'Parameters not specified'});
    } else {
        var token = getToken(req.headers);
        if(token) {
            var decoded = jwt.decode(token, config.secret);
            User.findOne({
                name: decoded.username
            }, function(err, user){
                const route = user.usersRoutes.find(r => r.date == req.body.date);
                route.name = req.body.newName;
                user.save(err => {
                    if(err){
                        return res.status(500).json({success: false, message: 'Cant save the name'});
                    } else {
                        return res.json({success: true, message: 'Name changed'});
                    }
                });
            });
        }
    }
}

exports.deleteRun = function(req, res) {
    if(!req.body.date){
        return res.status(404).json({success: false, message: 'Parameters not specified'});        
    } else {
        var token = getToken(req.headers);
        if(token) {
            var decoded = jwt.decode(token, config.secret);
            User.findOne({
                name: decoded.username
            }, function(err, user){
                const route = user.usersRoutes.find(r => r.date == req.body.date);
                const index = user.usersRoutes.indexOf(route);
                if(index > -1){
                    user.usersRoutes.splice(index, 1);
                    user.save(err => {
                        if(err){
                            return res.status(500).json({success: false, message: 'Cant save the name'});                        
                        } else {
                            return res.json({success: true, message: 'Item deleted'});                        
                        }
                    })
                }else {
                    return res.status(404).json({success: false, message: 'Route to be deleted was not found'});
                }
            })
        }
    }
}

exports.signUp = function(req, res) {
    if(!req.body.username || !req.body.password){
        return res.status(404).json({success: false, message: 'Please pass name and password.'});
    } else {
        var newUser =  new User({
            username: req.body.username,
            password: req.body.password
        }).save(function(err){
            if(err){
                return res.json({success: false, message: 'Username already exists'});
            } else {
                return res.json({success: true, message: 'Successfully created'});
            }
        })
    }
}

exports.authenticate = function(req, res) {
    User.findOne({
        username: req.body.username
    }, function(err, user) {
        if(err)
            res.status(500).json({success: false, message: 'Server error'});
        if(!user){
            return res.status(403).json({success: false, message: 'Authentication failed. User not found'});
        } else {
            user.comparePassword(req.body.password, function(err, isMatch){
                if(isMatch && !err){
                    var token = jwt.encode({name: user.username}, config.secret);
                    return res.json({success: true, token: 'JWT ' + token});
                } else {
                    return res.status(403).json({success: false, message: 'Authentication failed. Wrong password'});
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
                res.status(500).json({success: false, message: 'Server error'});
            if(!user){
                return res.status(403).json({success: false, message: 'Authentication failed, no user found'});
            } else {
                var id = generateId();
                var newRoute = new Route({
                    routeId: id,
                    points: req.body.points,
                    name: req.body.name,
                    bestTime: req.body.time,
                    bestUser: user.username,
                    recordDate: req.body.date,
                    distance: req.body.distance,
                });
                newRoute.save(function(err){
                    if(err){
                        return res.json({success: false, message: 'Cant save route '});
                    } else {
                        user.usersRoutes.push({
                            routeId: id, 
                            name: req.body.name,
                            timesPer100: req.body.times,
                            date: req.body.date,
                            time: req.body.time,
                        });
                        user.save(function(err){
                            if(err)
                                return res.json({success: false, message: 'Cant update user data' + err});
                            return res.json({success: true, message: 'Route added'});
                        })
                    }
                });
            }
        });
    } else {
        return res.status(403).json({success: false, message: 'Authentication failed. Wrong password'});
    }
}

exports.getRoute = function(req, res) {
    if(req.query.routeId === undefined)
        return res.status(404).json({success: false, message: 'No routes found'});
    Route.findOne({routeId: req.query.routeId}, function(err, route) {
        if(err)
            res.status(500).json({success: false, message: 'Server error'});
        if(route === null)
            return res.json({success: false, message: 'No routes found'});
        else{
            if(!req.query.date){
                User.findOne({username: route.bestUser}, function(err, user){
                    if(err)
                        return res.status(500).json({success: false, message: 'Server error'});
                    const userData = user.usersRoutes.find(r => r.routeId===route.routeId);
                    const result = {
                        time: route.bestTime,
                        distance: route.distance,
                        checkpoints: route.points,
                        times: userData.timesPer100,
                        name: route.name,
                        date: userData.date,
                        isNew: false,
                    }             
                    return res.json(result);  
                })
            }else {
                var token = getToken(req.headers);
                if(token) {
                    var decoded = jwt.decode(token, config.secret);
                    User.findOne({username: decoded.name}, function(err, user){
                        if(err)
                            return res.status(500).json({success: false, message: 'Server error'});
                        const userRun = user.usersRoutes.find(r => r.date == req.query.date);
                        const result = {
                            time: userRun.time,
                            distance: route.distance,
                            checkpoints: route.points,
                            times: userRun.timesPer100,
                            name: userRun.name,
                            date: userRun.date,
                            isNew: false,
                        }
                        return res.json(result);       
                    })
                } else {
                    return res.status(403).json({success: false, message: 'Authentication failed'});
                }
            }
            
        }
    })
}

exports.getAllUsers = function(req, res) {
    User.find({}, function(err, users){
        if(err)
            res.status(500).json({success: false, message: 'Server error'});
        if(users.length === 0)
            return res.json({success: false, message: 'No users found'});
        else
            return res.json({success: true, users: users});
    })
}

exports.getUsersRoutes = function(req, res) {
    User.findOne({username: req.params.username}, function(err, user){
        if(err)
            return res.status(404).json({success: false, message: 'No user found'});
        let skipNumber = Number.parseInt(req.query.skip);
        if(skipNumber === undefined)
            skipNumber = 0;
        const userRoutesData = user.usersRoutes.map(r => ({
            routeId: r.routeId,
            name: r.name,
            date: r.date,
            time: r.time,
        }));
        if(skipNumber>userRoutesData.length){
            return res.json({routes: []});
        }
        Route.find({routeId: { $in:
            userRoutesData.map(r => r.routeId)
            }
        }, 
        function(err, routes){
            if(err)
                return res.json({success: false, message: 'Can not find routes'});
            const routesData = [];
            if(routes.length === 0)
                return res.json({routes: []});
            let count = 0;
            userRoutesData
                .sort((r1, r2) => r1.date > r2.date ? -1 : 1)
                .slice(skipNumber, skipNumber+max)
                .forEach(usersRoute => {
                    const foundRoute = routes.find(function(route) {return route.routeId  === usersRoute.routeId});                
                    routesData.push({
                        id: foundRoute.routeId,
                        date: usersRoute.date,
                        seconds: usersRoute.time,
                        name: usersRoute.name,
                        distance: foundRoute.distance,
                    })
            })
            return res.json({totalCount: userRoutesData.length, routes: routesData});
        })
    })
}

exports.postAnotherRun = function(req, res) {
    User.findOne({username: req.params.username}, function(err, user){
        if(err)
            return res.status(404).json({success: false, message: 'No user found'});
        user.usersRoutes.push({
            routeId: req.body.routeId,
            name: req.body.name,
            timesPer100: req.body.times,
            date: req.body.date,
            time: req.body.time,
        });
        user.save(function(err){
            if(err)
                return res.json({success: false, message: 'Cant add new run'});
            Route.findOne({routeId: req.body.routeId}, function(err, route){
                if(err || route === null)
                    return res.json({success: false, message: 'Cant find given route'});
                if(route.bestTime > req.body.time){
                    route.bestTime = req.body.time;
                    route.bestUser = req.params.username;
                    route.recordDate = req.body.date;
                    route.save(function(err){
                        if(err)
                            return res.json({success: false, message: 'Cant update route'});
                        return res.json({success: true, message: 'New routes record!'});
                    })
                }else{
                    return res.json({success: true, message: 'New run added'});                    
                }
            })
        })
    })
}

exports.search = function(req, res) {
    let skipNumber = Number.parseInt(req.query.skip);
    if(skipNumber === undefined)
        skipNumber = 0;
    const searchCryteria = {
        searchedDistance: !req.query.maxDistance && !req.query.minDistance 
        ? {
            max: undefined,
            min: undefined,
        }
        : {
            max: !req.query.maxDistance ? Number.MAX_SAFE_INTEGER : Number.parseInt(req.query.maxDistance),
            min: !req.query.minDistance ? 0 : Number.parseInt(req.query.minDistance)
        },
        searchedUser: {username: req.query.username},
        searchedCircle: {
            lat: !req.query.lat ? undefined : parseFloat(req.query.lat), 
            lng: !req.query.lng ? undefined : parseFloat(req.query.lng), 
            radius: !req.query.radius ? undefined : parseFloat(req.query.radius)},
    }

    const searchMechanisms = {
        searchedDistance : function(routes, distance) {
            return new Promise((resolve, reject) => {
                if(routes.length !== 0){
                    const result = routes.filter(r => 
                        r.distance < distance.max && r.distance > distance.min );
                    resolve(result);
                }
                else
                    reject()
            })
        },
        searchedUser: function(routes, user){
            return new Promise((resolve, reject) => {
                if(routes.length === 0)
                    reject([])
                else{
                    User.findOne({username: user.username}, function(err, user){
                        if(err){
                            reject()                            
                        }
                        if(user === null){
                            resolve([])                            
                        }
                        else {
                            const routeIds = user.usersRoutes.map(r => r.routeId);
                            const result = routes.filter(r => routeIds.indexOf(r.routeId) > -1);
                            resolve(result);                            
                        }
                    })
                }
            })
            
        },
        searchedCircle: function(routes, searchedCircle){
            return new Promise((resolve, reject) => {
                if(routes.length !== 0){
                    const result = routes.filter(r => {
                    const start = r.points[0];
                    return (start.lat - searchedCircle.lat)*(start.lat - searchedCircle.lat)
                        + (start.lng - searchedCircle.lng)*(start.lng - searchedCircle.lng) 
                        < searchedCircle.radius*searchedCircle.radius; 
                    });
                    resolve(result);
                }else{
                    reject();
                }
            })
        }
    }

    Route.find({}, (err, routes) => {
        if(err)
            throw err;
        let i = Object.keys(searchCryteria).length;
        Object.keys(searchCryteria).forEach(k => {
            filterWith(searchCryteria[k], searchMechanisms[k], routes)
                .then((filteredResult) => {
                    routes = routes.filter(r => filteredResult.indexOf(r) > -1);
                    i--;
                    if(i === 0){
                        const results = routes.slice(skipNumber, skipNumber+max).map(r => 
                            ({
                                id: r.routeId,
                                name: r.name + ' - ' + r.bestUser,
                                seconds: r.bestTime,
                                distance: r.distance,
                                date: r.recordDate,
                            }));
                        return res.json({totalCount: routes.length, routes: results});
                    }
                })
                .catch(() => {
                    return res.json([]);
                })
        });
    })
}

async function filterWith(searchCryterium, searchMechanism, routes){
    let result = routes;
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

