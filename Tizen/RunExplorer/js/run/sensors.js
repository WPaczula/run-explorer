var initSensors = function(mapInit) {
		var self = this,
			segmentDistance,
			lastSegmentTime,
			
		// stopwatch
			StopWatch = {
			set: {},
			onChange: {},
			exit: {},
			watch: {},
		},
		
		// speed listener
		SpeedDistanceChangeListener = {
			bootDistance: 0,
			set: {},
			onChange: {},
			exit: {}
		},
		
		// geolocation listener
		GeolocationChangeListener = {
			watch: {},
			set: {},
			onChange: {},
			exit: {}
		},
		
		HeartRateChangeListener = {
			set: {},
			onChange: {},
			exit: {}
		};
		
		/**
		 * Function setting stopwatch
		 */
		StopWatch.set = function(){
			StopWatch.watch = setInterval(StopWatch.onChange, 1000);
		};
		
		/**
		 * Function updating stopwatch display
		 */
		StopWatch.onChange = function(){
			
			function convertToTime(secondsSum){
				var seconds = secondsSum % 60,
					minutes = parseInt(secondsSum / 60),
					hours = parseInt(secondsSum / 3600);
				
				console.log(seconds, minutes, hours);
				
				return displayAsTime(hours) + ':' + 
						displayAsTime(minutes) + ':' +  
						displayAsTime(seconds) ;
			}
			
			function displayAsTime(number){
				if(number === 0 ){
					return '00';
				}
				else{
					return number>9 ? number : '0'+number;
				}
					
			}
			
			self.data.main.time += 1;
			self.ui.mainpage.stopwatch.innerHTML = convertToTime(self.data.main.time);
		};
		
		/**
		 * Function invoked on finishing run 
		 */
		StopWatch.exit = function(){
			self.data.main.time = 0;
			clearInterval(StopWatch.watch);
		};
		
		/**
		 * Function invoked on pausing run
		 */
		StopWatch.pause = function(){
			clearInterval(StopWatch.watch);
		};
		
		/**
		 * Function setting up geolocation watch
		 */
		GeolocationChangeListener.set = function(){
			if(navigator.geolocation) {
				GeolocationChangeListener.watch = navigator.geolocation.watchPosition(GeolocationChangeListener.onChange, self.onerror);
				console.log('Geolocation sensor set');
			} else {
				self.ui.mainpage.location.innerHTML = 'Location not supported';
			}
		};
		
		/**
		 * Gets devices current location
		 */
		GeolocationChangeListener.onChange = function(position){
			self.data.main.position = {lat: position.coords.latitude, lng: position.coords.longitude};
			if(!self.map.ready){
				mapInit();
				self.map.init();
			}
			console.log(position);
			if(self.map.ready){
				if(self.sensors.running)
					self.map.updatePosition(self.data.map.myMap, self.data.main.position);
				else {
					self.map.showPosition(self.data.map.myMap, self.data.main.position);
				}
			}
		};
		
		/**
		 * Closes navigator watch
		 */
		GeolocationChangeListener.exit = function(){
			if(navigator.geolocation){
				navigator.geolocation.clearWatch(GeolocationChangeListener.watch);
				console.log('Geolocation sensor exit');
			}
		};
		
		/**
		 * Get device's current speed and accumulative pedometer data
		 */
		SpeedDistanceChangeListener.set = function () {
            try {
            	tizen.humanactivitymonitor.setAccumulativePedometerListener(SpeedDistanceChangeListener.onChange);
                console.log('Speed/distance sensor set');
            	segmentDistance = 0;
            	segmentTime = 0;
            	lastSegmentTime = 0;
            } catch (error) {
                self.onerror(error);
            }
        };
        
        /**
         *  A change callback to handle every time the pedometer data of the device is updated.
         */
        SpeedDistanceChangeListener.onChange = function (info) {
        	function round(n){
        		return Math.round(n*100)/100;
        	}
            self.data.main.speed = info.speed;
            if(SpeedDistanceChangeListener.bootDistance === 0)
            	SpeedDistanceChangeListener.bootDistance = info.accumulativeDistance;
            self.data.main.distance = Math.round(info.accumulativeDistance - SpeedDistanceChangeListener.bootDistance);
            	if(self.data.main.distance > segmentDistance){
                	self.data.main.checkpoints.push({
                		lat: self.data.main.position.lat,
                		lng: self.data.main.position.lng,
                		});
                	segmentDistance += segmentDistance;
                }
                if(self.data.main.distance > segmentTime){
                	self.data.main.times.push(self.data.main.time);
                	lastSegmentTime = self.data.main.time;
                	segmentTime += segmentTime;
                }
            console.log("SpeedDistance: ", info);
            self.ui.mainpage.speed.innerHTML = "Pace: " + self.data.main.speed + "km/h";
            self.ui.mainpage.distance.innerHTML = "Distance: " + round(self.data.main.distance / 1000) + "km";
        };
        
        /**
         * Stops gps speed sensor
         */
        SpeedDistanceChangeListener.exit = function(){
        	tizen.humanactivitymonitor.unsetAccumulativePedometerListener();
        	console.log('Speed sensor exit');
        };
        
        /**
         * Sets up heart rate listener
         */
        HeartRateChangeListener.set = function(interval){
        	if(tizen.systeminfo.getCapability('http://tizen.org/feature/sensor.heart_rate_monitor')){
        		var callback = tizen.humanactivitymonitor.start.bind(self, 'HRM', HeartRateChangeListener.onChange);
        		HeartRateChangeListener.watch = setInterval(callback, interval);
        		console.log('Heart rate sensor set');
        	} else {
        		self.ui.mainpage.heartRate.innerHTML = 'Heart rate not supported';
        	}		
        };
        
        /**
         * Handles heart rate changes, saves values bigger than 0
         */
        HeartRateChangeListener.onChange = function(heartRateInfo){
    		self.data.main.heartRate = heartRateInfo.heartRate;
        	self.ui.mainpage.heartRate.innerHTML = "HR: " + (self.data.main.heartRate > 0 ? self.data.main.heartRate : 0);
        };
        
        /**
         * Stops gps speed sensor
         */
        HeartRateChangeListener.exit = function(){
        	tizen.humanactivitymonitor.stop('HRM');
        };
        
        self.sensors.setup = function() {
        	GeolocationChangeListener.set(self.data.main.gpsMaxAge);
        	HeartRateChangeListener.set(self.data.main.heartCallbackInterval);
        }
        
        /**
         * Starts all sensors at once
         */
        self.sensors.start = function(){
        	StopWatch.set();
        	SpeedDistanceChangeListener.set(self.data.main.sampleInterval, self.data.main.callbackInterval);
        	self.sensors.running = true;
        };
        
        /**
         * Pauses all sensors at once
         */
        self.sensors.pause = function() {
        	StopWatch.pause();
        	self.sensors.running = false;
        	SpeedDistanceChangeListener.exit();
        };
        
        /**
         * Stops all sensors at once
         */
        self.sensors.stop = function() {
        	StopWatch.exit();
        	SpeedDistanceChangeListener.exit();
			GeolocationChangeListener.exit();
			HeartRateChangeListener.exit();
        };
	};