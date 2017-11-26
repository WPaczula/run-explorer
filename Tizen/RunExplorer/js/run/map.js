var initMap = function() {
	var self = this,
		routesCheckpointsMarkers = [],
		routesCheckpointsMarkersPassed = [],
		routeToBeat,
		routeBet,
		blue = "#0000FF",
		green = "#00FF00",
		pathLatLng = [];
	var routeData = localStorage.getItem('route');
	if(routeData !== undefined){
		pathLatLng = JSON.parse(routeData);
	}
	
	function createMarker(map, position){
		var marker = L.marker(position).addTo(map);
		return marker;
	}
	
	function moveCameraToMarker(map, marker){
		map.panTo(marker);
		setTimeout(function(){ map.invalidateSize();}, 400);
	}
	
	function didPassCheckpoint(currentPosition, checkpointPosition){
		var latDifference = Math.abs(currentPosition[0] - checkpointPosition[0]);
	    var lngDifference = Math.abs(currentPosition[1] - checkpointPosition[1]);
	    return latDifference < 0.0001 && lngDifference < 0.0001;
	}
	
	function convertPositionsForInvisibleMarkersOnMap(markersPositions, map){
		var routesConsecutiveMarkers = [];

	    for (var i = 0; i < markersPositions.length; i++) {
	        var position = markersPositions[i];
	        var marker = [position.lat, position.lng];
	        routesConsecutiveMarkers.push(marker);    
	    }
	    return routesConsecutiveMarkers;
	}
	
	function isFinished(checkpointsToBeat, checkpointsPassed){
	    return checkpointsToBeat.length === checkpointsPassed.length;
	}
	
	function normalizePosition(position){
		return [position.lat, position.lng];
	}
	
	function tryToGetNextCheckpoint(routesCheckpointsMarkers, routesCheckpointsMarkersPassed, routeBet, currentPosition){
	    if(isFinished(routesCheckpointsMarkers, routesCheckpointsMarkersPassed) !== true){
	        var nextCheckpoint = routesCheckpointsMarkers[routesCheckpointsMarkersPassed.length];
	        if(didPassCheckpoint(normalizePosition(currentPosition),
	        		nextCheckpoint)){
		        	routesCheckpointsMarkersPassed.push(nextCheckpoint);
	                routeBet.addLatLng(nextCheckpoint);
	        }
	    } else {
	    	self.data.map.completedGivenRoute = true;
	    	self.sensors.pause();
	    }
	}
		
	function drawNewCheckpoint(map, routesCheckpointsMarkersPassed, routeBet, position){
		routesCheckpointsMarkersPassed.push(position);
		routeBet.addLatLng(position);
	}
	
	function drawARouteBetweenMarkersOnMap(map, markers, color){
		var route = L.polyline(markers, {color: color, weight: 10}).addTo(map);
        return route;
	}
	
	function moveMarker(map, marker, position){
        marker.setLatLng(position);
		return marker;
	}
	
	function followThePath(map, position){
		self.data.map.currentPositionMarker = moveMarker(map, self.data.map.currentPositionMarker, position);
		tryToGetNextCheckpoint(routesCheckpointsMarkers, routesCheckpointsMarkersPassed, routeBet, position);
		moveCameraToMarker(self.data.map.myMap, normalizePosition(self.data.main.position));
	}
	
	function measureDistance(position1, position2){
		var R = 6378.137; // Radius of earth in KM
	    var dLat = position2.lat() * Math.PI / 180 - position1.lat() * Math.PI / 180;
	    var dLng = position2.lng() * Math.PI / 180 - position1.lng() * Math.PI / 180;
	    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(position1.lat() * Math.PI / 180) * Math.cos(position2.lat() * Math.PI / 180) *
	    Math.sin(dLng/2) * Math.sin(dLng/2);
	    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    var d = R * c;
	    return d * 1000; // meters
	}
	
	function makeOwnPath(map, position){
		self.data.map.currentPositionMarker = moveMarker(map, self.data.map.currentPositionMarker, position);
		drawNewCheckpoint(map, routesCheckpointsMarkersPassed, routeBet, position);
		moveCameraToMarker(self.data.map.myMap, normalizePosition(self.data.main.position));
	}
	
	self.map.init = function(){
		self.data.map.myMap = L.map(self.ui.mappage.map, normalizePosition(self.data.main.position), 17);
		L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
	    }).addTo(self.data.map.myMap);
		if(pathLatLng.length === 0){
			routesCheckpointsMarkersPassed = convertPositionsForInvisibleMarkersOnMap([self.data.main.position], self.data.map.myMap);
		}
		routesCheckpointsMarkers = convertPositionsForInvisibleMarkersOnMap(pathLatLng, self.data.map.myMap);
		routeToBeat = drawARouteBetweenMarkersOnMap(self.data.map.myMap, routesCheckpointsMarkers, blue, 0);
		routeBet = drawARouteBetweenMarkersOnMap(self.data.map.myMap, routesCheckpointsMarkersPassed, green, 1);
		self.data.map.currentPositionMarker = createMarker(self.data.map.myMap, self.data.main.position);
		self.data.map.myMap.setView(normalizePosition(self.data.main.position), 17);
		moveCameraToMarker(self.data.map.myMap, normalizePosition(self.data.main.position));
		self.map.updatePosition = (pathLatLng.length !== 0) ? followThePath : makeOwnPath;
		self.map.ready = true;
	};
	
	self.map.resize = function() {
		moveCameraToMarker(self.data.map.myMap, 
				self.data.map.currentPositionMarker.getLatLng()
				);
	};
	
	self.map.showPosition = function(map, position){
		self.data.map.currentPositionMarker = moveMarker(map, self.data.map.currentPositionMarker, position);
		moveCameraToMarker(self.data.map.myMap, normalizePosition(self.data.main.position));
		
	}
	
	self.map.reset = function(){
		routesCheckpointsMarkersPassed = [];
		routeBet.remove();
		routeBet = drawARouteBetweenMarkersOnMap(self.data.map.myMap, routesCheckpointsMarkersPassed, green, 1);
	};
};