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
    <!-- Favicon icon -->
    <title>SANCOM JOB APPLICATION PORTAL</title>
    <!-- Custom CSS -->
    <link href="dist/css/style.min.css" rel="stylesheet">
    <link href="assets/libs/parsley/parsley.css" rel="stylesheet">
    <link href="assets/libs/sweetalert2/dist/sweetalert2.min.css" rel="stylesheet">
    
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</head>

<body>
    <div class="main-wrapper">
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
        <!-- Preloader - style you can find in spinners.css -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Login box.scss -->
        <!-- ============================================================== -->
        <div class="auth-wrapper d-flex no-block justify-content-center align-items-center" style="background:url(assets/images/big/auth-bg.jpg) no-repeat center center;">
            <div class="auth-box">
                <div id="loginform">
                    <div class="logo">
                        <span class="db"><img src="assets/images/logo-icon.png" alt="logo" /></span>
                        <h5 class="font-medium m-b-20">Sign In TO SANCOM JOB PORTAL </h5>
                    </div>
                    <!-- Form -->
                    <div class="row">
                        <div class="col-12">
                            <form class="form-horizontal m-t-20" id="formlogin"  data-parsley-validate="">
                                <div class="input-group mb-3">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text" id="basic-addon1"><i class="ti-user"></i></span>
                                    </div>
                                    <input type="email" class="form-control form-control-lg" type="email" required="" id="userEmail" name="userEmail" placeholder="Enter Email" aria-label="Username" aria-describedby="basic-addon1">
                                </div>
                                <div class="input-group mb-3">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text" id="basic-addon2"><i class="ti-pencil"></i></span>
                                    </div>
                                    <input  class="form-control form-control-lg" type="password" required="" id="password"name="password" placeholder="Password"  aria-label="Password" aria-describedby="basic-addon1">
                                </div>
                               
                                <div class="form-group text-center">
                                    <div class="col-xs-12 p-b-20">
                                        <button class="btn btn-block btn-lg btn-info" type="submit">Log In</button>
                                    </div>
                                </div>
                              
                                <div class="form-group m-b-0 m-t-10">
                                    <div class="col-sm-12 text-center">
                                        Don't have an account? <a href="signup.jsp" class="text-info m-l-5"><b>Sign Up</b></a>
                                    </div>
                                </div>
                                
                                
                            </form>
                            
                            <form  method="GET" id="getform" >
            						<input type = "hidden" name = "relno" id = "relno" value="">
							</form>
                        </div>
                    </div>
                </div>
                
            </div>
        </div>
        <!-- ============================================================== -->
        <!-- Login box.scss -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Page wrapper scss in scafholding.scss -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Page wrapper scss in scafholding.scss -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Right Sidebar -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Right Sidebar -->
        <!-- ============================================================== -->
    </div>
    <!-- ============================================================== -->
    <!-- All Required js -->
    <!-- ============================================================== -->
    <script src="assets/libs/jquery/dist/jquery.min.js"></script>
    <!-- Bootstrap tether Core JavaScript -->
    <script src="assets/libs/popper.js/dist/umd/popper.min.js"></script>
    <script src="assets/libs/bootstrap/dist/js/bootstrap.min.js"></script>
     <script src="assets/libs/parsley/parsley.min.js"></script>
      <script src="assets/libs/sweetalert2/dist/sweetalert2.all.min.js"></script>
      <script src="custom.js"></script>
    <!-- ============================================================== -->
    <!-- This page plugin js -->
    <!-- ============================================================== -->
    <script>
    $('[data-toggle="tooltip"]').tooltip();
    $(".preloader").fadeOut();
    
    
    var pubkey=getPubKey();
    $(document).ready(function () {

        $("#formlogin").submit(function (event) {

            //stop submit the form, we will post it manually.
            event.preventDefault();

            fnSubmitForm();

        });

    });
    
    
    function fnSubmitForm(){
    	
    	var url="/restapi/login";
    	var relno="";
    	  var loginForm = {}
          loginForm["userEmail"] = $("#userEmail").val();
          loginForm["password"] = $("#password").val();
          loginForm["pubkey"] = pubkey;
  
    	var jVariables=JSON.stringify(loginForm);
    	
    	  $.ajax({
              beforeSend: function(xhr){  xhr.overrideMimeType( "text/plain; charset=x-user-defined" );},// Include this line to specify what Mime type the xhr response is going to be
              url: url,  type: "POST", dataType: "json", contentType : "application/json", data:jVariables,
              success: function (result) {
               if (result) {
                         if(result['statusCode']=='200'){
                             console.log("no error");
                           		relno=result.relno;
                           	
                           		
                           		if(result.details==="notpresent")
				                             Swal.fire({
				                             text: "You have successfully Login "+relno,
				                             icon: "success",
				                             type: "success",   
				                             showConfirmButton: true,
				                             confirmButtonText: "Ok",
				                             closeOnConfirm: true,
				                             timer: 1500
				
				                             }).then(function() {
				                            	 //redirect to different pages
				                            	  $('#relno').val(relno);
				                            	 if(result.details==="notpresent"){
				                            	 			window.location.href = 'personaldetails.jsp';
				                            	 	}else if (result.details==="present"){
				                            	 		window.location.href = 'applyjob.jsp';
				                            	 	}
				                             });
                           				
                               
                         }else if (result['error']=='incorrect') {
                             Swal.fire({
                         text: "Incorrect Login Credentials",
                         icon: "error",
                         type:  "error",})
                         
                         }else if (result['error']=='userdoesnotexist') {
                             Swal.fire({
                                 text: "Sorry User Doesn't Exist",
                                 icon: "error",
                                 type:  "error",})
                           }else if (result['statusCode']=='401') {
                               Swal.fire({
                                   text: "Sorry, You are unable to login at the moment",
                                   icon: "error",
                                   type:  "error",})
                             }
                     } 
                 }
             });
  
    }
    
  
    </script>
</body>

</html>