<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Tell the browser to be responsive to screen width -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>SANCOM JOB APPLICATION</title>
    <!-- Custom CSS -->
    <!--link href="assets/libs/datatables.net-bs4/css/dataTables.bootstrap4.css" rel="stylesheet"-->
    <link href="dist/css/style.css" rel="stylesheet">
    <link href="assets/extra-libs/DataTables/datatables.min.css" rel="stylesheet">
    <link href="assets/libs/parsley/parsley.css" rel="stylesheet">
    <link href="assets/libs/sweetalert2/dist/sweetalert2.min.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->


    <!-- ============================================================== -->
    <!-- All Jquery -->
    <!-- ============================================================== -->
    <script src="assets/libs/jquery/dist/jquery.min.js"></script>
    <!-- Bootstrap tether Core JavaScript -->
    <script src="assets/libs/popper.js/dist/umd/popper.min.js"></script>
    <script src="assets/libs/bootstrap/dist/js/bootstrap.min.js"></script>
    <!-- apps -->
    <script src="dist/js/app.min.js"></script>
    <script src="dist/js/app.init.light-sidebar.js"></script>
    <script src="dist/js/app-style-switcher.js"></script>
    <!-- slimscrollbar scrollbar JavaScript -->
    <script src="assets/libs/perfect-scrollbar/dist/perfect-scrollbar.jquery.min.js"></script>
    <script src="assets/extra-libs/sparkline/sparkline.js"></script>
    <!--Wave Effects -->
    <script src="dist/js/waves.js"></script>
    <!--Menu sidebar -->
    <script src="dist/js/sidebarmenu.js"></script>
    <!--Custom JavaScript -->
    <script src="dist/js/custom.min.js"></script>
    <script src="assets/extra-libs/DataTables/datatables.min.js"></script>
    <script src="custom.js"></script>
    <script src="assets/libs/parsley/parsley.min.js"></script>
     <script src="assets/libs/sweetalert2/dist/sweetalert2.all.min.js"></script>

    <!--This page JavaScript -->
</head>
<body onload="javascript:fnOnLoad();return false;">
    <!-- ============================================================== -->
    <!-- Preloader - style you can find in spinners.css -->
    <!-- ============================================================== -->
    <div class="preloader">
        <div class="lds-ripple">
            <div class="lds-pos"></div>
            <div class="lds-pos"></div>
        </div>
    </div>
    <!-- ============================================================== -->
    <!-- Main wrapper - style you can find in pages.scss -->
    <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Page wrapper  -->
        <!-- ============================================================== -->
        <div class="page-wrapper">
            <!-- ============================================================== -->
            <!-- Bread crumb and right sidebar toggle -->
            <!-- ============================================================== -->
             <div class="page-breadcrumb">
                <div class="row">
                    <div class="col-5 align-self-center">
                        <h4 class="page-title">View Applied Job</h4>
                        <div class="d-flex align-items-center">
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb">
                                	<li class="breadcrumb-item"><a href="#"></a>Apply Jobs</li>
                                    <li class="breadcrumb-item active" aria-current="page">View Applied Job</li>
                                </ol>
                            </nav>
                        </div>
                    </div>
                </div>
            </div>
            <!-- ============================================================== -->
            <!-- End Bread crumb and right sidebar toggle -->
            <!-- ============================================================== -->
            <!-- ============================================================== -->
            <!-- Container fluid  -->
            <!-- ============================================================== -->
            <div class="container-fluid">
                <!-- ============================================================== -->
                
                <!-- Row -->
                <div class="row">
                
               			 <div class="col-12">
								
									<div class="card card-hover">
										<div class="card-body">
											<h4 class="card-title">View Job Present</h4>
											<div id="displaytable" ></div>
											
										</div>
										
									</div>
					
						</div>
						<form  method="GET" id="getform" >
            						<input type = "hidden" name = "relno" id = "relno" value="">
							</form>
                
                </div>
                <!-- Row -->
            </div>
            <!-- ============================================================== -->
            <!-- End Container fluid  -->
            <!-- ============================================================== -->
            <!-- ============================================================== -->
            <!-- footer -->
            <!-- ============================================================== -->
			<%-- <jsp:include page="footer.html" /> --%>
            <!-- ============================================================== -->
            <!-- End footer -->
            <!-- ============================================================== -->
        </div>
        <!-- ============================================================== -->
        <!-- End Page wrapper  -->
        <!-- ============================================================== -->
  

<script>
//Custom JS
	 
    
    var pubkey=getPubKey();
    var relno=getUrlVars()["relno"];
    
    function fnLoad(){
    	var listHTML = '';
  	  $('#displaytable').html('');
    	
    	var url="/restapi/viewappliedjobs";
    	var jVariables= JSON.stringify({apikey:pubkey,relno:relno});
    	 $.ajax({
             beforeSend: function(xhr){  xhr.overrideMimeType( "text/plain; charset=x-user-defined" );},// Include this line to specify what Mime type the xhr response is going to be
             url: url,  type: "POST", dataType: "json", contentType : "application/json", data:jVariables,
             success: function (result) {
              if (result) {
                        if(result['statusCode']=='200'){
                        	$('#displaytable').html('');
 			             	console.log("Result is"+result);
 			                listHTML='<table id="example" class="display compact"style="width: 100%;">';
 			                
 			                listHTML+='<thead><tr><th class="numeric-cell">Application Id</th><th class="numeric-cell">JobID</th><th class="numeric-cell">Date of Application</th><th class="label-cell">Action</th></thead><tbody>';
 			                
 			                for (var i= 0; i <result.applicationid.length;i++) {
 			                listHTML+='<tr><td class="numeric-cell"><u>'+result.applicationid[i]+'</u></td><td class="numeric-cell">'+result.jobId[i]+'</td><td class="numeric-cell">'+result.createdon[i]+'</td><td class="numeric-cell"><button class="btn btn-danger" onclick="javascript:fnCancel('+result.applicationid[i]+')">Cancel Job Application</button></td></tr>'; 
 			                 } 
 			                 listHTML+='</tbody></table>';

 			                 $('#displaytable').append(listHTML); 
 			                 formTable();	
                              
                        }else if (result['error']=='nodata') {
                        	$('#displaytable').html('');
                        	listHTML='<h4 class="card-title">NO JOB APPLIED AT THE MOMENT</h4>';
                            $('#displaytable').append(listHTML);
                        }else if (result['statusCode']=='401') {
                              Swal.fire({
                                  text: "Sorry, we are unable to load jobs at the moment",
                                  icon: "error",
                                  type:  "error",})
                            }
                    } 
                }
            });
 
    	
    }
    
    function formTable(){
    	//alert('calling table');
    	$('#example').DataTable( {
        "paging":   false,
        "searching":   true,
        "arrow_right":     true,
        "bLengthChange": false,
        "ordering": false
    } );

    }
    
    function fnCancel(applicationid){
    	var url="/restapi/deselectjob";
    	var jVariables= JSON.stringify({apikey:pubkey,applicationid,applicationid});
    	
    	Swal.fire({
            text:'Are you sure you want cancel this Job Application?',
            type:"info",		
            showCancelButton: true,
            showCancelButton: true,
            confirmButtonColor: '#FF0000',
  	        cancelButtonColor: '#C0C0C0', 
  	    	  confirmButtonText: 'Delete',
  	        cancelButtonText: 'Cancel', 
  	        closeOnConfirm: true
          }).then((result) => {
          	if (result.value) {
          		 $.ajax({
                     beforeSend: function(xhr){  xhr.overrideMimeType( "text/plain; charset=x-user-defined" );},// Include this line to specify what Mime type the xhr response is going to be
                     url: url,  type: "POST", dataType: "json", contentType : "application/json", data:jVariables,
                     success: function (result) {
                      if (result) {
                                if(result['statusCode']=='200'){
                                    console.log("no error");
       				                             Swal.fire({
       				                             html: "You have cancelled the job Application",
       				                             icon: "success",
       				                             type: "success",   
       				                             showConfirmButton: true,
       				                             confirmButtonText: "Ok",
       				                             closeOnConfirm: true,
       				                             timer: 1500
       				
       				                             }).then(function(){
       				                            	 location.reload();
       				                            	 	
       				                             });
                                  				
                                      
                                }else if (result['statusCode']=='401') {
                                      Swal.fire({
                                          text: "Sorry, we are unable to cancel the job at the moment",
                                          icon: "error",
                                          type:  "error",})
                                    }
                            } 
                        }
                    });
          		
          	}
          }); 
    	
    	
    }
	  
	  
	  
	  
</script>


</body>

	
</body>
</html>


  