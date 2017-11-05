var initConnection = function(){
	var self = this;
	
	function createHTML(log_string)
	{
	    var content = document.getElementById("toast-content");
	    content.innerHTML = log_string;
	    window.tau.openPopup("#toast");
	}

	self.connection.connectionListener = {
	    /* Remote peer agent (Consumer) requests a service (Provider) connection */
	    onrequest: function (peerAgent) {

	        createHTML("peerAgent: peerAgent.appName<br />" +
	                    "is requsting Service conncetion...");

	        /* Check connecting peer by appName*/
	        if (peerAgent.appName === "RunExplorerConsumer") {
	        	self.connection.SAAgent.acceptServiceConnectionRequest(peerAgent);
	            createHTML("Service connection request accepted.");

	        } else {
	        	self.connection.SAAgent.rejectServiceConnectionRequest(peerAgent);
	            createHTML("Service connection request rejected.");

	        }
	    },

	    /* Connection between Provider and Consumer is established */
	    onconnect: function (socket) {
	        var onConnectionLost,
	            dataOnReceive;

	        createHTML("Service connection established");

	        /* Obtaining socket */
	        self.connection.SASocket = socket;

	        onConnectionLost = function onConnectionLost (reason) {
	            createHTML("Service Connection disconnected due to following reason:<br />" + reason);
	        };

	        /* Inform when connection would get lost */
	        self.connection.SASocket.setSocketStatusListener(onConnectionLost);

	        dataOnReceive =  function dataOnReceive (channelId, data) {

	            if (!self.connection.SAAgent.channelIds[0]) {
	                createHTML("Something goes wrong...NO CHANNEL ID!");
	                return;
	            }
	            
	            /* Send new data to Consumer */
	            localStorage.setItem('route', data);
	            self.connection.SASocket.sendData(self.connection.SAAgent.channelIds[0], 'Route set');
	            tau.changePage('#controls-page');
	            console.log('jump brah');
	        };

	        /* Set listener for incoming data from Consumer */
	        self.connection.SASocket.setDataReceiveListener(dataOnReceive);
	    },
	    onerror: function (errorCode) {
	        createHTML("Service connection error<br />errorCode: " + errorCode);
	    }
	};

	function requestOnSuccess (agents) {
	    var i = 0;

	    for (i; i < agents.length; i += 1) {
	        if (agents[i].role === "PROVIDER") {
	            createHTML("Service Provider found!<br />" +
	                        "Name: " +  agents[i].name);
	            self.connection.SAAgent = agents[i];
	            break;
	        }
	    }

	    /* Set listener for upcoming connection from Consumer */
	    self.connection.SAAgent.setServiceConnectionListener(self.connection.connectionListener);
	}

	function requestOnError (e) {
	    createHTML("requestSAAgent Error" +
	                "Error name : " + e.name + "<br />" +
	                "Error message : " + e.message);
	}

	/* Requests the SAAgent specified in the Accessory Service Profile */
	webapis.sa.requestSAAgent(requestOnSuccess, requestOnError);

	(function(tau) {
	    var toastPopup = document.getElementById('toast');

	    toastPopup.addEventListener('popupshow', function(){
	        setTimeout(function () {
	            tau.closePopup();
	        }, 3000);
	    }, false);
	})(window.tau);
}


