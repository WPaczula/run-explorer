var initMap = function() {
	var self = this,
		routesCheckpointsMarkers = [],
		routesCheckpointsMarkersPassed = [],
		routeToBeat,
		routeBet,
		blue = "#0000FF",
		green = "#00FF00",
		pathLatLng = [
//	                  {lat: 50.249930, lng: 18.565919},
//	                  {lat: 50.250253, lng: 18.566552},
//	                  {lat: 50.250415, lng: 18.566706},
//	                  {lat: 50.250747, lng: 18.566952},
//	                  {lat: 50.250722, lng: 18.567749},
//	                  {lat: 50.250677, lng: 18.568315}
	                  ];
	var routeData = localStorage.getItem('route');
	console.log(routeData);
	if(routeData !== undefined){
		pathLatLng = JSON.parse(routeData);
	}
	
	function createMarker(map, position){
		var marker = new google.maps.Marker({
			position: position,
			map: map
		});
		return marker;
	}
	
	function moveCameraToMarker(map, marker){
		map.panTo(marker.getPosition());
	}
	
	function didPassCheckpoint(currentPosition, checkpointPosition){
	    var latDifference = Math.abs(currentPosition.lat() - checkpointPosition.lat());
	    var lngDifference = Math.abs(currentPosition.lng() - checkpointPosition.lng());
	    return latDifference < 0.0001 && lngDifference < 0.0001;
	}
	
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
	
	function isFinished(checkpointsToBeat, checkpointsPassed){
	    return checkpointsToBeat.length === checkpointsPassed.length;
	}
	
	function tryToGetNextCheckpoint(routesCheckpointsMarkers, routesCheckpointsMarkersPassed, routeBet, currentPosition){
	    if(isFinished(routesCheckpointsMarkers, routesCheckpointsMarkersPassed) !== true){
	        var nextCheckpoint = routesCheckpointsMarkers[routesCheckpointsMarkersPassed.length];
	        if(didPassCheckpoint(convertPosition(currentPosition),
	            nextCheckpoint.getPosition())){
	                routesCheckpointsMarkersPassed.push(nextCheckpoint);
	                var path = routeBet.getPath();
	                path.push(nextCheckpoint.getPosition());
	        }
	    } else {
	    	self.map.completedGivenRoute = true;
	    }
	}
		
	function drawNewCheckpoint(map, routesCheckpointsMarkersPassed, routeBet, position){
		var marker = new google.maps.Marker({
            position: position,
            map: map,
            visible: false
        });
		routesCheckpointsMarkersPassed.push(marker);
		var path = routeBet.getPath();
		path.push(marker.getPosition());
	}
	
	function convertPosition(position){
		return {lat: function(){return position.lat;},
				lng: function(){return position.lng;}};
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
	
	function moveMarker(map, marker, position){
		var toBeDeleted = marker//self.data.map.currentPositionMarker;
		marker = createMarker(map, position);
		toBeDeleted.setMap(null);
		return marker;
	}
	
	function followThePath(map, position){
		console.log(position);
		self.data.map.currentPositionMarker = moveMarker(map, self.data.map.currentPositionMarker, position);
		tryToGetNextCheckpoint(routesCheckpointsMarkers, routesCheckpointsMarkersPassed, routeBet, position);
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
		console.log(position);
		self.data.map.currentPositionMarker = moveMarker(map, self.data.map.currentPositionMarker, position);
		drawNewCheckpoint(map, routesCheckpointsMarkersPassed, routeBet, position);
	}
	
	self.map.init = function(){
		self.data.map.myMap = new google.maps.Map(self.ui.mappage.map, {
	        zoom: 17,
	        center: self.data.main.position,
			disableDefaultUI: true
	    });
		if(pathLatLng.length === 0){
			routesCheckpointsMarkersPassed = convertPositionsForInvisibleMarkersOnMap([self.data.main.position], self.data.map.myMap);
		}
		routesCheckpointsMarkers = convertPositionsForInvisibleMarkersOnMap(pathLatLng, self.data.map.myMap);
		routeToBeat = drawARouteBetweenMarkersOnMap(self.data.map.myMap, routesCheckpointsMarkers, blue, 0);
		routeBet = drawARouteBetweenMarkersOnMap(self.data.map.myMap, routesCheckpointsMarkersPassed, green, 1);
		self.data.map.currentPositionMarker = createMarker(self.data.map.myMap, self.data.main.position);
		moveCameraToMarker(self.data.map.myMap, self.data.map.currentPositionMarker);
		self.map.ready = true;
	};
	
	self.map.resize = function() {
		setTimeout(google.maps.event.trigger(self.data.map.myMap, 'resize'), 1000);
		moveCameraToMarker(self.data.map.myMap, self.data.map.currentPositionMarker);
	};
	
	self.map.updatePosition = (pathLatLng.length !== 0) ? followThePath : makeOwnPath;
	
	self.map.reset = function(){
		routesCheckpointsMarkersPassed = [];
		routeBet.setMap(null);
		routeBet = drawARouteBetweenMarkersOnMap(self.data.map.myMap, routesCheckpointsMarkersPassed, green, 1);
	};
};