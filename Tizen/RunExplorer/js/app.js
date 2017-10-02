(function(){
	var myapp = {
			// constatns
			SECOND_TO_MILISECOND: 1000,
			
			// preference keys
	        PREFERENCE_KEY_IS_RUNNING: 'isRunning',
	        PREFERENCE_KEY_SPEED: 'speed',
	        PREFERENCE_KEY_LOCATION: 'location',
	        PREFERENCE_KEY_HEARTRATE: 'heartrate',
			
			// main page
			MAIN_PAGE: 'main-page',
			MAIN_SPEED: 'content-speed',
			MAIN_LOCATION: 'content-location',
			MAIN_HEARTRATE: 'content-heartrate',
			MAIN_ERROR: 'content-error',
			
			// map page
			MAP_PAGE: 'map-page',
			MAP_DIV: 'map',
				
			// used data	
			data: {
				main: {
					speed: 0,
					location: {},
					heartRate: 0,
					sampleInterval: 1000,
					callbackInterval: 1000,
					heartCallbackInterval: 3000,
					gpsMaxAge: 5000
				},
				map:{
					myMap: {},
					currentPositionMarker: {},
				}
			},
			
			// page navigation
			navigation: {
				rotarydetentHandler: {}
			},
			
			// ui elements
			ui: {
				mainpage: {
					speed: {},
					location: {},
					heartRate: {},
					error: {},
				},
				mappage: {
					map: {}
				}
			},
			
			sensors: {
				start: {},
				stop: {},
				// speed listener
				SpeedChangeListener: {
					set: {},
					onChange: {},
					exit: {}
				},
				
				// geolocation listener
				GeolocationChangeListener: {
					watch: {},
					set: {},
					onChange: {},
					exit: {}
				},
				
				HeartRateChangeListener: {
					set: {},
					onChange: {},
					exit: {}
				}
			},
			
			map: {
				init: {},
				updatePosition: {}
			},
			
			// on error and exit
			onerror: {},
			exit: {}
	};
	
	var initUI = function() {
		var self = this;
		
		/**
		 * Setting 'isRunning' as false and exits the app.
		 */
		self.exit = function() {
			try {
				self.sensors.stop();
				tizen.preference.setValue(self.PREFERENCE_KEY_IS_RUNNING, false);
				console.log('Everything turned off');
				tizen.application.getCurrentApplication().exit();
			}catch (ignore) {}
		};
		
		// Init all ui elements
		self.ui.mainpage.speed = document.getElementById(self.MAIN_SPEED);
		self.ui.mainpage.location = document.getElementById(self.MAIN_LOCATION);
		self.ui.mainpage.heartRate = document.getElementById(self.MAIN_HEARTRATE);
		self.ui.mainpage.error = document.getElementById(self.MAIN_ERROR);
		
		self.ui.mappage.map = document.getElementById(self.MAP_DIV);
	
		
		/**
		 * Sets on error action which shows error's name
		 * @param {Tizen.WebAPIError} err - error to handle
		 */
		self.onerror = function(err) {
			self.ui.mainpage.error.innerHTML = err.name;
		};
		
		
		self.navigation.rotarydetentHandler = function(e) {
			var pages = [self.MAIN_PAGE, self.MAP_PAGE];
			var change = false;
			var currentIndex = pages.findIndex(function(name){
													return checkPage(name);
												});
			
			if(e.detail.direction === 'CW'){
				if(currentIndex !== pages.length-1){
					tau.changePage("#" + pages[currentIndex+1]);
					change=true;
				}
			}else if(currentIndex !== 0){
				tau.changePage("#" + pages[currentIndex-1]);
				change=true;
			}
			
			if(currentIndex === pages.indexOf(self.MAP_PAGE) && change){
				self.map.init();
			}
		};
		console.log('UI set');
	};
	
	var initSensors = function() {
		var self = this;
		
		/**
		 * Function setting up geolocation watch
		 */
		self.sensors.GeolocationChangeListener.set = function(maxAge){
			if(navigator.geolocation) {
				self.sensors.GeolocationChangeListener.watch = navigator.geolocation.watchPosition(
																self.sensors.GeolocationChangeListener.onChange,
																self.onerror,
																{maximumAge: maxAge});
				console.log('Geolocation sensor set');
			} else {
				self.ui.mainpage.location.innerHTML = 'Location not supported';
			}
		};
		
		/**
		 * Gets devices current location
		 */
		self.sensors.GeolocationChangeListener.onChange = function(position){
			self.data.main.position = {lat: position.coords.latitude, lng: position.coords.longitude};
			if(checkPage(self.MAP_PAGE) && self.data.map.myMap !== undefined){
				self.map.updatePosition(self.data.map.myMap, self.data.main.position);
			}
		};
		
		/**
		 * Closes navigator watch
		 */
		self.sensors.GeolocationChangeListener.exit = function(){
			if(navigator.geolocation){
				navigator.geolocation.clearWatch(self.sensors.GeolocationChangeListener.watch);
				console.log('Geolocation sensor exit');
			}
		};
		
		/**
		 * Get device's current speed
		 */
		self.sensors.SpeedChangeListener.set = function (sampleInterval, callbackInterval) {
            try {
                var options = {
                	'sampleInterva': sampleInterval * self.SECOND_TO_MILLISECOND,
                	'callbackInterval': callbackInterval * self.SECOND_TO_MILLISECOND
                };
                tizen.humanactivitymonitor.start('GPS', self.sensors.SpeedChangeListener.onChange, self.onerror, options);
                console.log('Speed sensor set');
            } catch (error) {
                self.onerror(error);
            }
        };
        
        /**
         *  A change callback to handle every time the GPS data of the device is updated.
         */
        self.sensors.SpeedChangeListener.onChange = function (info) {
            	var newData = info.gpsInfo[info.gpsInfo.length-1];
            	console.log('Pace', newData.speed);
                self.data.main.speed = newData.speed;
                self.ui.mainpage.speed.innerHTML = "Pace: " + self.data.main.speed + "km/h";
        };
        
        /**
         * Stops gps speed sensor
         */
        self.sensors.SpeedChangeListener.exit = function(){
        	tizen.humanactivitymonitor.stop('GPS');
        	console.log('Speed sensor exit');
        };
        
        /**
         * Sets up heart rate listener
         */
        self.sensors.HeartRateChangeListener.set = function(interval){
        	if(tizen.systeminfo.getCapability('http://tizen.org/feature/sensor.heart_rate_monitor')){
        		var callback = tizen.humanactivitymonitor.start.bind(self, 'HRM', self.sensors.HeartRateChangeListener.onChange);
        		self.sensors.HeartRateChangeListener.watch = setInterval(callback, interval);
        		console.log('Heart rate sensor set');
        	} else {
        		self.ui.mainpage.heartRate.innerHTML = 'Heart rate not supported';
        	}		
        };
        
        /**
         * Handles heart rate changes, saves values bigger than 0
         */
        self.sensors.HeartRateChangeListener.onChange = function(heartRateInfo){
    		self.data.main.heartRate = heartRateInfo.heartRate;
        	self.ui.mainpage.heartRate.innerHTML = "HR: " + (self.data.main.heartRate > 0 ? self.data.main.heartRate : 0);
        };
        
        /**
         * Stops gps speed sensor
         */
        self.sensors.HeartRateChangeListener.exit = function(){
        	tizen.humanactivitymonitor.stop('HRM');
        };
        
        /**
         * Starts all sensors at once
         */
        self.sensors.start = function(){
        	self.sensors.SpeedChangeListener.set(myapp.data.main.sampleInterval, myapp.data.main.callbackInterval);
        	self.sensors.GeolocationChangeListener.set(myapp.data.main.gpsMaxAge);
        	self.sensors.HeartRateChangeListener.set(myapp.data.main.heartCallbackInterval);
        };
        
        /**
         * Stops all sensors at once
         */
        self.sensors.stop = function() {
        	self.sensors.SpeedChangeListener.exit();
			self.sensors.GeolocationChangeListener.exit();
			self.sensors.HeartRateChangeListener.exit();
        };
	};

	var initMap = function() {
		var self = this,
			routesCheckpointsMarkers = [],
			routesCheckpointsMarkersPassed = [],
			routeToBeat,
			routeBet,
			blue = "#0000FF",
			green = "#00FF00",
			pathLatLng = [
		                  {lat: 50.250253, lng: 18.566550},
		                  {lat: 50.250253, lng: 18.566552},
		                  {lat: 50.250415, lng: 18.566706},
		                  {lat: 50.250747, lng: 18.566952},
		                  {lat: 50.250722, lng: 18.567749},
		                  {lat: 50.250677, lng: 18.568315}
		                  ];
		
		function createMarker(map, position){
			var marker = new google.maps.Marker({
				position: position,
				map: map
			});
			return marker;
		};
		
		function moveCameraToMarker(map, marker){
			map.panTo(marker.getPosition());
		};
		
		
		function convertPositionsForInvisibleMarkersOnMap(markersPositions, map){
		    var routesConsecutiveMarkers = [];

		    for (var i = 0; i < markersPositions.length; i++) {
		        var position = markersPositions[i];
		        var marker = new google.maps.Marker({
		            position: position,
		            map: map,
		            visible: false
		        });
		        routesConsecutiveMarkers.push(marker);    
		    }
		    return routesConsecutiveMarkers;
		}
		
		function drawARouteBetweenMarkersOnMap(map, markers, color, zIndex){
		    var route = new google.maps.Polyline({
		        path: markers.map(function(marker){
		            return marker.getPosition();
		        }),
		        geodesic: true,
		        strokeColor: color,
		        strokeOpacity: 1.0,
		        strokeWeight: 10
		    });

		    route.set('zIndex', zIndex);

		    route.setMap(map);
		    return route;
		}
		
		self.map.init = function(){
			self.data.map.myMap = new google.maps.Map(self.ui.mappage.map, {
		        zoom: 17,
		        center: self.data.main.position || {lat: 50.24, lng: 18.56},
				disableDefaultUI: true
		    });
			
			routesCheckpointsMarkers = convertPositionsForInvisibleMarkersOnMap(pathLatLng, self.data.map.myMap);
			routeToBeat = drawARouteBetweenMarkersOnMap(self.data.map.myMap, routesCheckpointsMarkers, blue, 0);
			
			self.data.map.currentPositionMarker = createMarker(self.data.map.myMap, self.data.main.position || {lat: 50.24, lng: 18.56});
		};
		
		self.map.updatePosition = function(map, position){
			console.log(position);
			var toBeDeleted = self.data.map.currentPositionMarker;
			self.data.map.currentPositionMarker = createMarker(map, position);
			moveCameraToMarker(map, self.data.map.currentPositionMarker);
			toBeDeleted.setMap(null);
		};
	};
	
	/**
	 * Checks if given page is active and uses right callback
	 */
	var checkPage = function(name){
		var page = document.getElementsByClassName('ui-page-active')[0],
			pageid = page ? page.id : "";
		return pageid === name;
	};
	
	/**
     *  Sets a function for window.onload .
     */
    window.onload = function () {
    	console.log('Load start');
    	
    	initUI.call(myapp);
    	initMap.call(myapp);
    	initSensors.call(myapp);
    	
    	myapp.sensors.start();

    	window.addEventListener('rotarydetent', myapp.navigation.rotarydetentHandler);
        window.addEventListener('tizenhwkey', function (ev) {
            if (ev.keyName === "back") {
            	
	    		if(checkPage(myapp.MAIN_PAGE)){
	    			myapp.exit();
	    		}else{
	    			window.history.back();
	    		}
            }
        });
        
        tizen.preference.setValue(myapp.PREFERENCE_KEY_IS_RUNNING, true);
    };
})();