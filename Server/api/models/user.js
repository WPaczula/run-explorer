'use strict';
var mongoose = require('mongoose');
var bcrypt = require('bcrypt')
var Schema = mongoose.Schema;


var UserSchema = new Schema({
  username: {
    type: String,
    required: true,
    unique: true
  },
  password: {
    type: String,
    required: true
  },
  routesIds: [
    {
        routeId: {
            type: Number,
            required: true
        },
        timesPer100: [
            {
                type: Number
            }
        ]
    }
  ]
});

UserSchema.pre('save', function (next) {
    var user = this;
    if(this.isModified('password') || this.isNew){
        bcrypt.genSalt(10, function (err, salt) {
            if(err){
                return next(err);
            }
            bcrypt.hash(user.password, salt, function(err, hash){
                if(err){
                    return next(err);
                }
                user.password = hash;
                next()
            });
        });
    }else {
        return next();
    }
});

UserSchema.methods.comparePassword = function(password, cb) {
    bcrypt.compare(password, this.password, function (err, isMath) {
        if(err){
            return cb(err);
        }
        cb(null, isMath);
    });
};

module.exports = mongoose.model('Users', UserSchema);