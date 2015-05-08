<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<title>Social Bookmarking System</title>
<link rel="stylesheet" type="text/css"
	href="http://fonts.googleapis.com/css?family=Vollkorn">
<link href="<c:url value="css/bootstrap.min.css" />" rel="stylesheet">
<link href="<c:url value="css/main.css"/>" rel="stylesheet">
<link href="<c:url value="css/font-awesome.css"/>" rel="stylesheet"
	type="text/css">
<link href="<c:url value="css/fotorama.css"/>" rel="stylesheet">
<link href="<c:url value="css/animate.css"/>" rel="stylesheet">
</head>
<body>
	<div class="container">
		<div id="navbar" class="navbar navbar-default ">
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li id="login-menu"><a href="#login" data-toggle="modal"
						data-target="#login-modal">Log In</a></li>
					<li id="register-menu"><a href="#register" data-toggle="modal"
						data-target="#register-modal">Register</a></li>
				</ul>
			</div>
		</div>
		<div class="row text-center">
			<div class="col-xs-12">
				<div class="tlt-h h_c">
					<ul class="texts">
						<li>Social Bookmarking Service</li>
						<li>Tag Recommendation</li>
					</ul>
				</div>
				<div class="tlt-p p_c" data-in-effect="fadeIn">
					<ul class="texts">
						<li>A centralized online service which enables users to add,
							 annotate, edit <br />and share bookmarks of web documents
						</li>
						<li>The task of predicting folksonomy tags for a given user
							 and Bookmark, based on <br/>a Topic Model implemented by LDA
						</li>
					</ul>
				</div>
			</div>
		</div>
		<div class="row text-center">
			<div class="fotorama" data-autoplay="1500" data-width="500"
				data-height="300" data-fit="contain">
				<img src="images/show5.jpg" title="photo5"
					data-caption="Tag Recommendation"> <img
					src="images/show2.jpg" title="photo2" data-caption="Recommend User">
				<img src="images/show3.jpg" title="photo3"
					data-caption="Discover Boookmark"> <img
					src="images/show4.jpg" title="photo4" data-caption="Trending">
				<img src="images/show1.jpg" title="photo1"
					data-caption="User Dashboard">
			</div>
		</div>
	</div>
	<!-- Login Modal -->
	<div class="modal fade" id="login-modal" tabindex="-1" role="dialog"
		aria-labelledby="loginModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title text-center" id="loginModalLabel">Log
						In</h4>
					<div class="text-danger text-center error-message"></div>
				</div>
				<form action="login" method="post" role="form" id="login-form">
					<div class="modal-body">
						<div class="form-group">
							<label for="login-email" class="control-label">Email: </label> <input
								type="text" class="form-control input-sm " id="login-email"
								placeholder="Email" />
							<div class="error-message"></div>
						</div>
						<div class="form-group">
							<label for="login-password" class="control-label">Password:
							</label> <input type="password" class="
								form-control input-sm"
								id="login-password" placeholder="Your Password" />
							<div class="error-message"></div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-info btn-block">Login</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!-- Register Modal -->
	<div class="modal fade" id="register-modal" tabindex="-1" role="dialog"
		aria-labelledby="registerModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title text-center" id="registerModalLabel">Register</h4>
				</div>
				<!-- Start Register Form -->
				<form role="form" id="register-form">
					<div class="modal-body">
						<div class="row">
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<label for="first-name">First Name: </label> <input type="text"
										class="form-control input-sm" id="first-name"
										placeholder="First Name">
								</div>
							</div>
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<label for="last-name" class="control-label">Last Name:
									</label> <input type="text" class="form-control input-sm"
										id="last-name" placeholder="Last Name">
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="email-register">Email: </label> <input type="text"
								class="form-control input-sm" id="email-register"
								placeholder="Your Email" />
						</div>
						<div class="form-group">
							<label for="password">Password: </label> <input type="password"
								class="form-control input-sm" id="password"
								placeholder="Your Password" />
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-info btn-block">Register</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<script src="<c:url value="js/jquery.js"/>"></script>
	<script src="<c:url value="js/bootstrap.min.js"/>"></script>
	<script src="<c:url value="js/fotorama.js"/>"></script>
	<script src="<c:url value="js/jquery.fittext.js"/>"></script>
	<script src="<c:url value="js/jquery.lettering.js"/>"></script>
	<script src="<c:url value="js/jquery.textillate.js"/>"></script>
	<script src="<c:url value="js/main.js"/>"></script>
</body>
</html>