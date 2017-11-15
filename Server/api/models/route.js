'use strict';
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RouteSchema = new Schema({
    routeId: {
        type: String,
        required: true,
    },
    points: [{
        lat : Number,
        lng : Number,
    }],
    bestTime: {
        type: Number,
        required: true,
    },
    bestUser: {
        type: String,
        required: true,
    },
    distance: {
        type: Number,
        required: true,
    }
});

module.exports = mongoose.model('Routes', RouteSchema);