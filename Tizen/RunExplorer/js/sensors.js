var initSensors = function() {
		var self = this;
		
		/**
		 * Function setting stopwatch
		 */
		self.sensors.StopWatch.set = function(){
			self.sensors.StopWatch.watch = setInterval(self.sensors.StopWatch.onChange, 1000);
		}
		
		/**
		 * Function updating stopwatch display
		 */
		self.sensors.StopWatch.onChange = function(){
			
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
			
			self.sensors.StopWatch.time += 1;
			self.ui.mainpage.stopwatch.innerHTML = convertToTime(self.sensors.StopWatch.time);
		}
		
		/**
		 * Function invoked on finishing run 
		 */
		self.sensors.StopWatch.exit = function(){
			self.sensors.StopWatch.time = 0;
			clearInterval(self.sensors.StopWatch.watch);
		}
		
		/**
		 * Function invoked on pausing run
		 */
		self.sensors.StopWatch.pause = function(){
			clearInterval(self.sensors.StopWatch.watch);
		}
		
		/**
		 * Function setting up geolocation watch
		 */
		self.sensors.GeolocationChangeListener.set = function(callbackInterval){
			if(navigator.geolocation) {
				self.sensors.GeolocationChangeListener.watch = navigator.geolocation.watchPosition(self.sensors.GeolocationChangeListener.onChange, self.onerror);
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
			console.log(position);
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
        	self.sensors.StopWatch.set();
        	self.sensors.SpeedChangeListener.set(self.data.main.sampleInterval, self.data.main.callbackInterval);
        	self.sensors.GeolocationChangeListener.set(self.data.main.gpsMaxAge);
        	self.sensors.HeartRateChangeListener.set(self.data.main.heartCallbackInterval);
        };
        
        /**
         * Pauses all sensors at once
         */
        self.sensors.pause = function() {
        	self.sensors.StopWatch.pause();
        };
        
        /**
         * Stops all sensors at once
         */
        self.sensors.stop = function() {
        	self.sensors.StopWatch.exit();
        	self.sensors.SpeedChangeListener.exit();
			self.sensors.GeolocationChangeListener.exit();
			self.sensors.HeartRateChangeListener.exit();
        };
	};