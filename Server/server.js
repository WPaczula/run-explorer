var express = require('express'),
    app = express(),
    bodyParser = require('body-parser'),
    passport = require('passport'),
    config = require('./config/database'),
    mongoose = require('mongoose'),
    port = process.env.PORT || 3000;
    require('./api/models/user');
    require('./api/models/route');

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

app.use(passport.initialize());
require('./config/passport')(passport);

mongoose.Promise = global.Promise;
mongoose.connect(config.database);

var routes = require('./api/routes/runExplorerRoutes');
routes(app);

app.listen(port);

console.log('Server started on: ' + port);