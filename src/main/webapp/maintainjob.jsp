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
    <title>SANCOM MAINTAIN JOB</title>
    <!-- Custom CSS -->
    <!--link href="assets/libs/datatables.net-bs4/css/dataTables.bootstrap4.css" rel="stylesheet"-->
    <link href="dist/css/style.css" rel="stylesheet">
    <link href="assets/extra-libs/DataTables/datatables.min.css" rel="stylesheet">
    <link href="assets/libs/parsley/parsley.css" rel="stylesheet">
    <link href="assets/libs/sweetalert2/dist/sweetalert2.min.css" rel="stylesheet">
     <link href="assets/libs/parsley/parsley.css" rel="stylesheet">

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
                        <h4 class="page-title">View and Create Jobs</h4>
                        <div class="d-flex align-items-center">
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb">
                                	<li class="breadcrumb-item"><a href="#"></a>Create New Jobs</li>
                                    <li class="breadcrumb-item active" aria-current="page">View Jobs</li>
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
											
											 <h6 class="card-subtitle">Create New Jobs <br> <button type="button" class="btn waves-effect waves-light btn-rounded btn-success" id="btnaddmodal" data-toggle="modal" data-target="#addModal">Add Jobs</button> &nbsp;&nbsp;&nbsp; <button type="button" class="btn waves-effect waves-light  btn-info" onclick="fnViewApplicants()">View Applicants</button> </h6>
											
											<div id="displaytable" ></div>
											
										
										
									</div>
					
						</div>
                
                
                	<div class="modal fade bs-example-modal-lg" id="addModal" tabindex="-1" role="dialog" aria-labelledby="editModalLabel" aria-hidden="true">
									  <div class="modal-dialog" role="document">
										<div class="modal-content">
										  <div class="modal-header">
											<h5 class="modal-title" id="editModalLabel">Add New Job </h5>
											<button type="button" class="close" data-dismiss="modal" aria-label="Close">
											  <span aria-hidden="true">&times;</span>
											</button>
										  </div>
										  <div class="modal-body">											
												<div class="col-12">
													<div class="card">
														<div class="card-body">
															<h4 class="card-title">Create Job</h4>
															  <form  class="form-horizontal m-t-20" id="submitjob" data-parsley-validate="">
															  
															   <div class="row">
						                                            <div class="col-md-6">
						                                                <div class="form-group">
						                                                    <label class="control-label">Job Name</label>
						                                                    <input type="text" name="jobName" id="jobName" class="form-control" placeholder="Job Name" required="">
						                                                   </div>
						                                            </div>
                                            
							                                            <div class="col-md-6">
							                                                <div class="form-group has-danger">
							                                                    <label class="control-label">Job Type</label>
							                                                    <input type="text" name="jobType" id="jobType" required="" class="form-control form-control-danger" placeholder="Job Type">
							                                                  </div>
							                                            </div>
                                       							 </div>
                                       							    <div class="row">
						                                            <div class="col-md-12">
						                                                <div class="form-group">
						                                                    <label class="control-label">Job Description</label>
						                                                    <input type="textarea" name="jobDescription" id="jobDescription" class="form-control" placeholder="Job Name" required="">
						                                                   </div>
						                                            </div>
                                       							 </div>
															   <div class="row ">
						                                              <div class="col-md-6">
									                                                <div class="form-group">
									                                                    <label class="control-label">Education Level</label>
									                                                    <select class="form-control custom-select" id ="educationLevel" required="" name="educationLevel" data-placeholder="Choose a Category" tabindex="1">
									                                                         <option value="" disabled selected>Please select</option>
									                                                        <option value="Post Graduate">Post Graduate</option>
									                                                        <option value="Graduate">Graduate</option>
									                                                        <option value="High School">High School</option>
									                                                    </select>
									                                                    <small class="form-control-feedback"> Select your Education Level </small>
									                                                </div>
									                                            </div>
                                            
							                                            <div class="col-md-6">
							                                                <div class="form-group has-danger">
							                                                    <label class="control-label">Year Of Experience Required</label>
							                                                    <input type="text" name="yearOfExperience" id="yearOfExperience" required="" class="form-control form-control-danger" placeholder="Year of Experience">
							                                                  </div>
							                                            </div>
							                                          
							                                            
                                       							 </div>
															   <div class="row ">
															     <div class="col-md-6">
							                                                <div class="form-group has-danger">
							                                                    <label class="control-label">Status</label>
											                                              <select name="status" id="status" required class="form-control">
																							<option value="" disabled selected>Please select</option>
																							<option value="active">Active</option>
																							<option value="inactive">Inactive</option>
																						</select>
							                                                  </div>
							                                            </div>
						                                            <div class="col-md-6">
						                                                <div class="form-group">
						                                                    <label class="control-label">Interview Date</label>
						                                                    <input type="date" name="interviewDate" id="interviewDate" class="form-control" placeholder="Date" required="">
						                                                   </div>
						                   	                         </div>
                                            				</div>
                                             
                                            			 <div class="row">
							                                            <div class="col-md-4">
							                                                <div class="form-group has-danger">
							                                                    <label class="control-label">Interview Start Time</label>
							                                                    <input type="time" name="interviewStartTime" id="interviewStartTime" required="" class="form-control form-control-danger" placeholder="Start Time">
							                                                  </div>
							                                            </div>
							                                            <div class="col-md-4">
							                                                <div class="form-group has-danger">
							                                                    <label class="control-label">Interview End Time</label>
							                                                    <input type="time" name="interviewEndTime" id="interviewEndTime" required="" class="form-control form-control-danger" placeholder="End Time">
							                                                  </div>
							                                            </div>
                                       							 </div>
											
																<div class="text-xs-right">
																	<button class="btn btn-block btn-lg btn-info" type="button" id="btnaddjob" onclick="javascript:fnSubmitJob();return false;"><span>Submit</span></button>								
																	<button type="reset" class="btn btn-inverse" data-dismiss="modal">Cancel</button>
																</div>
																	
															</form>
														</div>
													</div>
												</div>											
										  </div>
										  <div class="modal-footer">
											<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
										  </div>
										</div>
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

            $('#submitjob').parsley();

    });
    
    var pubkey=getPubKey();
    
    function fnOnLoad(){
    	var listHTML = '';
   	 
  	  $('#displaytable').html('');
    	
    	var url="/restapi/viewalljobs";
    	var jVariables= JSON.stringify({apikey:pubkey});
    	 $.ajax({
             beforeSend: function(xhr){  xhr.overrideMimeType( "text/plain; charset=x-user-defined" );},// Include this line to specify what Mime type the xhr response is going to be
             url: url,  type: "POST", dataType: "json", contentType : "application/json", data:jVariables,
             success: function (result) {
              if (result) {
                        if(result['statusCode']=='200'){
                        
                        	$('#displaytable').html('');
 			             	console.log("Result is"+result.jobid);
 			             
 			                listHTML='<table id="example" class="display compact"style="width: 100%;">';
 			                
 			                listHTML+='<thead><tr><th class="numeric-cell">JobID</th><th class="numeric-cell">Job Name</th><th class="label-cell">Job Description</th><th class="label-cell">Year Of Experience</th> <th class="label-cell">Interview Date</th> <th class="label-cell"> Inteview Start Time</th><th class="label-cell"> Inteview End Time</th><th class="label-cell"> Job Type</th><th class="label-cell">Status</th><th class="label-cell">Action</th></thead><tbody>';
 			                
 			                for (var i= 0; i <result.jobid.length;i++) { 
 			                listHTML+='<tr><td class="numeric-cell">'+result.jobid[i]+'</td><td class="numeric-cell">'+result.jobname[i]+'</td><td class="numeric-cell">'+result.jobdescription[i]+'</td><td class="numeric-cell">'+result.yearsofexperience[i]+'</td><td class="numeric-cell">'+result.interviewdate[i]+'</td><td class="numeric-cell">'+result.interviewstarttime[i]+'</td><td class="numeric-cell">'+result.interviewendtime[i]+'</td><td class="numeric-cell">'+result.jobtype[i]+'</td><td class="numeric-cell">'+result.status[i]+'</td><td class="numeric-cell"><button class="btn btn-danger" onclick="fnDelete(\''+result.jobid[i]+'\')">Delete</button></td></tr>'; 
 			                 } 
 			                 listHTML+='</tbody></table>';

 			                 $('#displaytable').append(listHTML); 
 			                 formTable();	
                              
                        }else if (result['error']=='nojobs') {
                        	$('#displaytable').html('');
                        	listHTML='<h4 class="card-title">NO JOB PRESENTS !!!</h4>';
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
    
    
    function fnSubmitJob(){
    	
    	var instance = $('#submitjob').parsley();
        if(instance.isValid()==false){
        	
        	return;
         }
    	var url="/restapi/createnewjobs";
    	var jobDescription=$("#jobDescription").val();
    	var jobName=$("#jobName").val();
    	var yearOfExperience=$("#yearOfExperience").val();
    	var educationLevel=$("#educationLevel").val();
    	var interviewDate=$("#interviewDate").val();
    	var interviewStartTime=$("#interviewStartTime").val();
    	var interviewEndTime=$("#interviewEndTime").val();
    	var jobType=$("#jobType").val();
    	var status=$("#status").val();
  
  
    	var jVariables= JSON.stringify({ jobDescription: jobDescription, jobName:jobName, yearOfExperience:yearOfExperience,
    		educationLevel:educationLevel, interviewDate:interviewDate, interviewStartTime:interviewStartTime,interviewEndTime:interviewEndTime,
    		jobType:jobType,status:status,apikey:pubkey});
    	alert(jVariables);
    	  $.ajax({
              beforeSend: function(xhr){  xhr.overrideMimeType( "text/plain; charset=x-user-defined" );},// Include this line to specify what Mime type the xhr response is going to be
              url: url,  type: "POST", dataType: "json", contentType : "application/json", data:jVariables,
              success: function (result) {
               if (result) {
                         if(result['statusCode']=='200'){
                             console.log("no error");
				                             Swal.fire({
				                             html: "Job Saved Succefully",
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
                                   text: "Sorry, we are unable to save the job at the moment",
                                   icon: "error",
                                   type:  "error",})
                             }
                     } 
                 }
             });
  
    }
	  
    function fnDelete(jobid){
    	
    	var url="/restapi/deletejob";
    	var jVariables= JSON.stringify({apikey:pubkey,jobid:jobid});
    	
    	Swal.fire({
            text:'Are you sure you want to delete this job?',
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
       				                             html: "Job Deleted Succefully",
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
                                          text: "Sorry, we are unable to delete the job at the moment",
                                          icon: "error",
                                          type:  "error",})
                                    }
                            } 
                        }
                    });
          		
          	}
          }); 
    	
    	
    }
	  
   function fnViewApplicants(){
	   
	   window.location.href="viewjobapplicants.jsp"
   }
    
</script>


</body>

	
</body>
</html>


  