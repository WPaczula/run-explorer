'use strict';

var mongoose = require('mongoose'),
    User = mongoose.model('Users'),
    jwt = require('jwt-simple'),
    config = require('../../config/database');

exports.signUp = function(req, res) {
    if(!req.body.username || !req.body.password){
        res.json({success: false, msg: 'Please pass name and password.'});
    } else {
        var newUser =  new User({
            username: req.body.username,
            password: req.body.password
        }).save(function(err){
            if(err){
                res.json({success: false, msg: 'Username already exists'});
            } else {
                res.json({success: true, msg: 'Successfully created'});
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
            return res.status(403).send({success: false, msg: 'Authentication failed. User not found'});
        } else {
            user.comparePassword(req.body.password, function(err, isMatch){
                if(isMatch && !err){
                    var token = jwt.encode(user, config.secret);

                    res.json({success: true, token: 'JWT ' + token});
                } else {
                    return res.status(403).send({success: false, msg: 'Authentication failed. Wrong password'});
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
            name: decoded.name
        }, function(err, user){
            if(err) throw err;

            if(!user){
                return res.status(403).send({success: false, msg: 'Authentication failed, no user found'});
            } else {
                return res.status(200).send({success: true, ayyy: 'Yoooo wasspupin'});
            }
        });
    } else {
        return res.status(403).send({success: false, msg: 'Authentication failed. Wrong password'});
    }
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
