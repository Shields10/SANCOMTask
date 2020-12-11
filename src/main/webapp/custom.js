function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function getPubKey(){ return 'W3ZJKULZZSNP3YMUX24A';}

function fnLogout(){
	Swal.fire({
        text:'Are you sure you want Log out?',
        type:"info",		
        showCancelButton: true,
        showCancelButton: true,
        confirmButtonColor: '#0000ff',
	        cancelButtonColor: '#C0C0C0', 
	    	  confirmButtonText: 'Yes',
	        cancelButtonText: 'No', 
	        closeOnConfirm: true
      }).then((result) => {
      	if (result.value) {
      		window.location.replace("index.jsp");
      	}
      });
	
}