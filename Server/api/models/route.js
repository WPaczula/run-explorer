'use strict';
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RouteSchema = new Schema({
    routeId: {
        type: String,
        required: true,
        unique: true,
        index: true,
    },
    points: [{
        lat : Number,
        lng : Number,
    }],
    name: {
        type: String,
        required: true,
    },
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