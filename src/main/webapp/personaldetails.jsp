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
<body>
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
                        <h4 class="page-title">SANCOM Job Application Form</h4>
                        <div class="d-flex align-items-center">
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb">
                                	<li class="breadcrumb-item"><a href="#">Applicant's  Details</a></li>
                                	<li class="breadcrumb-item"> <a><i class="fa fa-power-off m-r-5 m-l-5" onclick="fnLogout()"></i> Logout</a></li>
                                    <li class="breadcrumb-item active" aria-current="page">Please Input Your Details</li>
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
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header bg-info">
                                <h4 class="mb-0 text-white">Enter Your Personal Details</h4>
                            </div> 
                            <form  class="form-horizontal m-t-20" id="personaldetails" data-parsley-validate="">
                                <div class="card-body">
                                    <h4 class="card-title">Applicant's Details</h4>
                                </div>
                                <hr>
                                <div class="form-body">
                                    <div class="card-body">
                                        <div class="row pt-3">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label class="control-label">First Name</label>
                                                    <input type="text" name="userFirstName" id="userFirstName" class="form-control" placeholder="Enter First Name" required="">
                                                   </div>
                                            </div>
                                            <!--/span-->
                                            <div class="col-md-6">
                                                <div class="form-group has-danger">
                                                    <label class="control-label">Last Name</label>
                                                    <input type="text" name="userLastName" id="userLastName" required="" class="form-control form-control-danger" placeholder="Enter Last Name">
                                                  </div>
                                            </div>
                                            <!--/span-->
                                        </div>
                                        <!--/row-->
                                        <div class="row">
                                              <div class="col-md-6">
                                                <div class="form-group">
                                                    <label class="control-label">Phone Number</label>
                                                    <input type="number" id="userPhoneNumber" name="userPhoneNumber" required="" class="form-control" placeholder="Enter Your Phone Number">
                                                   </div>
                                            </div>
                                            <!--/span-->
                                             
                                            <!--/span-->
                                        </div>
                                        <!--/row-->
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label class="control-label">Education Level</label>
                                                    <select class="form-control custom-select" id ="userEducationLevel" required="" name="userEducationLevel" data-placeholder="Choose a Category" tabindex="1">
                                                         <option value="" disabled selected>Please select</option>
                                                        <option value="Post Graduate">Post Graduate</option>
                                                        <option value="Graduate">Graduate</option>
                                                        <option value="High School">High School</option>
                                                    </select>
                                                    <small class="form-control-feedback"> Select your Education Level </small>
                                                </div>
                                            </div>
                                            <!--/span-->
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label class="control-label">Years Of Experience</label>
                                                    <input type="number" id="yearsOfExperience"  required="" name="yearsOfExperience" class="form-control" placeholder="Enter your Years of Experience">
                                                   </div>
                                            </div>
                                            <!--/span-->
                                        </div>
                                        <!--/row-->
                                    </div>
                                    <hr>
                                   
                                    <div class="form-actions">
                                        <div class="card-body">
                                            <input type="submit" class="btn btn-success">
                                        
                                        </div>
                                    </div>
                                </div>
                            </form>
                            
                             <form  method="GET" id="getform" >
            						<input type = "hidden" name = "relno" id = "relno" value="">
							</form>
                        </div>
                    </div>
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
	  $(document).ready(function () {

        $("#personaldetails").submit(function (event) {

            //stop submit the form, we will post it manually.
            event.preventDefault();

            fnSubmitForm();
            $('#personaldetails').parsley();
        });

    });
    
    var pubkey=getPubKey();
    var relno=getUrlVars()["relno"];
    function fnSubmitForm(){
    	
    	var instance = $('#personaldetails').parsley();
        if(instance.isValid()==false){
        	
        	return;
         }
        	
    	
    	
    	
    	var url="/restapi/registerdetails";
    	var userRelationshipNo=relno;
    	var userFirstName=$("#userFirstName").val();
    	var userLastName=$("#userLastName").val();
    	var userPhoneNumber=$("#userPhoneNumber").val();
    	var userEducationLevel=$("#userEducationLevel").val();
    	var yearsOfExperience=$("#yearsOfExperience").val();
    	
  
  
    	var jVariables= JSON.stringify({ userRelationshipNo: userRelationshipNo, userFirstName:userFirstName, userLastName:userLastName,
    		userPhoneNumber:userPhoneNumber, userEducationLevel:userEducationLevel, yearsOfExperience:yearsOfExperience, apikey:pubkey});
    	
    	  $.ajax({
              beforeSend: function(xhr){  xhr.overrideMimeType( "text/plain; charset=x-user-defined" );},// Include this line to specify what Mime type the xhr response is going to be
              url: url,  type: "POST", dataType: "json", contentType : "application/json", data:jVariables,
              success: function (result) {
               if (result) {
                         if(result['statusCode']=='200'){
                             console.log("no error");
				                             Swal.fire({
				                             html: "Details Saved Successfully <br> Proceed to apply available jobs",
				                             icon: "success",
				                             type: "success",   
				                             showConfirmButton: true,
				                             confirmButtonText: "Ok",
				                             closeOnConfirm: true,
				                             timer: 1500
				
				                             }).then(function(){
				                            	      $('#relno').val(relno);
				                            	      $('#getform').attr('action', 'applyjob.jsp'); $( "#getform" ).submit();
				                            	 		
				                            	 	
				                             });
                           				
                               
                         }else if (result['statusCode']=='401') {
                               Swal.fire({
                                   text: "Sorry, You are unable to save your details at the moment",
                                   icon: "error",
                                   type:  "error",})
                             }
                     } 
                 }
             });
  
    }
	  
	  
	  
</script>


</body>

	
</body>
</html>


  