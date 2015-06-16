<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>Social Bookmarking System</title>
<link rel="stylesheet" type="text/css"
	href="http://fonts.googleapis.com/css?family=Vollkorn">
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/metisMenu.min.css" rel="stylesheet">
<link href="css/sb-admin-2.css" rel="stylesheet">
<link href="css/panel.css" rel="stylesheet">
<link href="css/jquery.urlive.css" rel="stylesheet">
<link href="css/jquery-ui.css" rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet" type="text/css">
<link href="css/star-rating.css" rel="stylesheet" media="all"
	type="text/css" />
<link href="css/introjs.css" rel="stylesheet">
<link href="css/introjs-nazanin.css" rel="stylesheet">
<link href="css/animate.css" rel="stylesheet">
</head>
<script>
<c:if test="${sessionScope.sortBy != null}">
	var sortBy = <c:out value="${sessionScope.sortBy}"/>;
</c:if>
</script>
<body>
	<div id="wrapper">
		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation"
			style="margin-bottom: 0">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<input type="hidden" class="userID" name="userID"
					value="<c:out value="${userID}" />" /> <a class="navbar-brand"
					href="."><c:out value="${firstName} ${lastName}" /></a>
			</div>
			<!-- /.navbar-header -->
			<ul class="nav navbar-top-links navbar-right">
				<li id="feed-back" data-toggle="modal" data-target="#feedback-modal"><a
					href="#"><i class="fa fa-comments-o"></i> Feedback</a></li>
				<li class="active" id="log-out"><a href="#">Log out</a></li>
			</ul>
			<!-- /.navbar-top-links -->
			<div class="navbar-default sidebar" role="navigation">
				<div class="sidebar-nav navbar-collapse">
					<ul class="nav" id="side-menu">
						<li class="sidebar-search">
							<div class="input-group custom-search-form">
								<input type="text" id="search-input" class="form-control"
									placeholder="Search..." data-toggle="tooltip"
									data-placement="right" data-html="true"
									data-original-title="Search tips: <br/> - #tagname - Search a specific tag <br/>
									 - @username - Search a specific user <br /> - keyword - Search a specific keyword<br/>">
								<span class="input-group-btn">
									<button id="search-button" class="btn btn-default"
										type="button">
										<i class="fa fa-search"></i>
									</button>
								</span>
							</div> <!-- /input-group -->
						</li>
						<li><a href="#" id="my-bookmarks-menu"><i
								class="fa fa-align-justify fa-fw"></i> My Bookmarks</a></li>
						<li><a href="network" id="network-menu"><i
								class="fa fa-users fa-fw"></i> Network</a></li>
						<li><a href="discover" id="discover-menu"><i
								class="fa fa-globe fa-fw"></i> Discover</a></li>
						<li><a href="trending" id="trending-menu"><i
								class="fa fa-star-o fa-fw"></i> Trending</a></li>
						<li><a href="evaluation" id="evaluation-menu"><i
								class="fa fa-area-chart fa-fw"></i> Evaluation</a></li>
						<li><a href="#" id="add-bookmark-menu" data-toggle="modal"
							data-target="#add-bookmark-modal"><i
								class="fa fa-plus-square fa-fw"></i> Add Bookmark</a></li>
						<li><a href="settings" id="setting-menu"><i
								class="fa fa-cog fa-fw"></i> Setting</a></li>
					</ul>
				</div>
			</div>
		</nav>
		<!-- Page Content -->
		<div id="page-wrapper">
			<div class="container-fluid">
				<div class="row page-header">
					<!-- =========================== -->
					<!-- Default page -->
					<!-- =========================== -->
					<div class="dashboard show">
						<div class="col-lg-2">
							<img src="images/user.png" class="img-thumbnail"
								alt="User Default Avatar"
								style="min-height: 100px; height: 100px;">
						</div>
						<div class="col-lg-10">

							<h2>
								<c:out value="${firstName} ${lastName}" />
							</h2>
							<h4 class="text-info">
								<c:out value="${user.bookmarkCount}" />
								Bookmark
								<c:out value="${user.followingCount}" />
								Following
								<c:out value="${user.followerCount}" />
								Follower
							</h4>
						</div>
					</div>
					<!-- =========================== -->
					<!-- Click vào discover -->
					<!-- =========================== -->
					<div class="discover hidden">
						<div class="row">
							<div class="col-lg-5">
								<h2>Please setting tag subscription:</h2>
							</div>
							<div class="col-lg-3">
								<button class="btn btn-primary btn-block"
									style="margin-top: 21px" data-toggle="modal"
									data-target="#subscription-modal">Add Tag Subscription</button>
							</div>
						</div>

						<div id="subscription-tags"></div>
						<div id="default-subscription-tags">Your most used tags:</div>
					</div>
					<!-- =========================== -->
					<!-- Click vào Network -->
					<!-- =========================== -->
					<div class="network hidden">
						<div class="row text-center">
							<h3 class="text-info">
								Recommend Users <span><i style="cursor: pointer;"
									class="fa fa-question fa-border" data-toggle="tooltip"
									data-placement="right" data-html="true"
									data-original-title="4 Users will be recommended based on their interest (topic + tags): <br/> - Same topic count: how many same topic was used <br/>
									 - Same tag count - How many same tag was used <br />"></i></span>
							</h3>

						</div>
						<div class="row row-centered">
							<c:if test="${not empty recommendUsers}">
								<c:forEach var="recommendUser" items="${recommendUsers}">
									<div class="col-md-15 col-sm-3 col-centered user">
										<div class="thumbnail clearfix">
											<img src="images/user.png" class="pull-left"
												alt="User Default Avatar"
												style="min-height: 60px; height: 60px; min-width: 60px; width: 60px; margin-right: 5px; margin-bottom: 15px;">
											<div>
												<input type="hidden" name="userID" class="userID"
													value="<c:out value="${recommendUser.userID}" />">
												<p class="text-center">
													<c:out
														value="${recommendUser.firstName}
													${recommendUser.lastName}" />
												</p>
												<c:set var="tooltip_tag" value="Top Used Tags (Used times):" />
												<c:set var="sharp" value="#" />
												<c:forEach var="t" items="${recommendUser.mostUsedTags}">
													<c:set var="tooltip_tag"
														value="${tooltip_tag} <br /><code>${sharp}${t.tag}</code> - (${t.weight})" />
												</c:forEach>
												<small style="font-size: 75%"> Top used tags: <i
													class="fa fa-info-circle" style="cursor: pointer;"
													data-toggle="tooltip" data-placement="right"
													data-html="true"
													data-original-title="<c:out value="${tooltip_tag}"/>"></i>
												</small><br />
												<c:set var="tooltip_topic" value="Top Used Topics:" />
												<c:forEach var="topic"
													items="${recommendUser.mostUsedTopics}">
													<c:set var="tooltip_topic"
														value="${tooltip_topic} <br />- <code>${topic}</code>" />
												</c:forEach>
												<small style="font-size: 75%"> Top used topics: <i
													class="fa fa-info-circle" style="cursor: pointer;"
													data-toggle="tooltip" data-placement="right"
													data-html="true"
													data-original-title="<c:out value="${tooltip_topic}"/>"></i>
												</small>
											</div>

										</div>
										<div class="text-center">
											<button class="btn btn-default btn-xs btn-success follow"
												type="button">
												<i class="fa fa-user-plus"></i> Follow
											</button>
										</div>
									</div>
								</c:forEach>
							</c:if>
							<c:if test="${empty recommendUsers}">
								<p class="text-warning">No suitable user for recommend.
									Please post more bookmarks and set tags!</p>
							</c:if>
						</div>
					</div>
					<!-- =========================== -->
					<!-- Khi Filter Tag vào Search  -->
					<!-- =========================== -->
					<div class="search hidden">
						<h3 class="text-center text-info"></h3>
					</div>
					<!-- ========== Click Trending ========== -->
					<div class="trending hidden">
						<div class="row text-center">
							<h3 class="text-info">Top Most Used Tags</h3>
							<div id="myCanvasContainer">
								<canvas width="500" height="150" id="myCanvas">
								 </canvas>
							</div>
						</div>
						<ul id="tagList">
							<c:if test="${not empty tagWeights}">
								<c:forEach var="tagWeight" items="${tagWeights}">
									<c:if test="${tagWeight.weight < 5}">
										<li><a href="#" style="font-size: 10px">${tagWeight.tag}</a></li>
									</c:if>
									<c:if
										test="${(tagWeight.weight >= 5) && (tagWeight.weight < 10)}">
										<li><a href="#" style="font-size: 15px">${tagWeight.tag}</a></li>
									</c:if>
									<c:if test="${(tagWeight.weight >= 10)}">
										<li><a href="#" style="font-size: 20px">${tagWeight.tag}</a></li>
									</c:if>
								</c:forEach>
							</c:if>
						</ul>
					</div>
					<div class="settings hidden">
						<div class="row">
							<div class="col-lg-12">
								<div>
									<ul class="nav nav-justified">
										<li id="system-users"><a href="#">System Users</a></li>
										<li id="change-password"><a href="#">Change Password</a></li>
										<li id="profile"><a href="#">Profile</a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
					<!-- =========================== -->
					<!-- Khi Xem system evaluation  -->
					<!-- =========================== -->
					<div class="evaluation hidden">
						<h2 class="text-center text-primary">System Online Evaluation</h2>
					</div>
				</div>
				<div class="page-content">
					<!-- =========================== -->
					<!-- Default page -->
					<!-- =========================== -->
					<div class="dashboard show">
						<div style="text-align: center;">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
					<!-- =========================== -->
					<!-- Click vào discover -->
					<!-- =========================== -->
					<div class="discover hidden">
						<div style="text-align: center;">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
					<!-- =========================== -->
					<!-- Click vào Network -->
					<!-- =========================== -->
					<div class="network hidden">
						<div class="show" id="network-waiting" style="text-align: center;">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
					<!-- Khi Filter Tag vào Search  -->
					<div class="search hidden">
						<div style="text-align: center;">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
					<!-- ========== Click Trending ========== -->
					<div class="trending hidden">
						<div style="text-align: center;">
							<i class="fa fa-spinner fa-pulse fa-5x"></i>
						</div>
					</div>
					<div class="settings hidden">
						<div class="row">
							<div class="hide" id="settings-loading"
								style="text-align: center;">
								<i class="fa fa-spinner fa-pulse fa-5x"></i>
							</div>
							<div class="hide" id="system-users-div"></div>
							<div class="col-md-5 col-md-offset-4 hide"
								id="change-password-div">
								<form id="change-password-form">
									<div class="form-group">
										<label class="control-label" for="old-password">Old
											Password:</label> <input type="password" class="form-control"
											id="old-password" placeholder="Old Password">
									</div>
									<div class="form-group">
										<label class="control-label" for="new-password">New
											Password:</label> <input type="password" class="form-control"
											id="new-password" placeholder="New Password">
									</div>
									<div class="form-group">
										<label class="control-label" for="new-password-confirm">New
											Password Confirm:</label> <input type="password" class="form-control"
											id="new-password-confirm" placeholder="New Password Confirm">
									</div>
									<button type="submit" class="btn btn-info btn-block">Change
										Password</button>
								</form>
							</div>
						</div>
					</div>
					<div class="evaluation hidden center-block">
						<div class="A1-result row" style="margin-top: 15px">
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A1. Quality of Recommended Items
										(Tags, Bookmark, Users) Evaluation</h3>
								</div>
								<div class="row" style="margin-top: 100px; text-align: center;">
									<div class="col-lg-6">
										<div id="A11" style="height: 300px; width: 100%;"></div>
									</div>
									<div class="col-lg-6">
										<div id="A12" style="height: 300px; width: 100%;"></div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div id="A15" style="width: 100%; height: 300px"></div>
									</div>
									<div class="col-md-6">
										<div id="A134" style="width: 100%; height: 300px"></div>
									</div>
								</div>
							</div>
						</div>
						<!-- <div class="A2-result row" style="margin-top: 15px">
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A2. Interaction Adequacy
										Evaluation</h3>
								</div>
								<div class="row" style="margin-top: 100px; text-align: center;">

								</div>
							</div>
						</div>
						<div class="A3-result row" style="margin-top: 15px">
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A3. Interface Adequacy Evaluation</h3>
								</div>
								<div class="row" style="margin-top: 100px; text-align: center;">

								</div>
							</div>
						</div>
						<div class="A4-result row" style="margin-top: 15px">
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A4. Perceived Ease of Use
										Evaluation</h3>
								</div>
								<div class="row" style="margin-top: 100px; text-align: center;">

								</div>
							</div>
						</div>
						<div class="A5-result row" style="margin-top: 15px"> 
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A5. Perceived Usefulness
										Evaluation</h3>
								</div>
								<div class="row" style="margin-top: 100px; text-align: center;">

								</div>
							</div>
						</div> -->
						<div class="A6-result row" style="margin-top: 15px">
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A2. Control / Transparency
										Evaluation</h3>
								</div>
								<div class="row" style="margin-top: 100px;">
									<div class="col-lg-8" style="text-align: center;">
										<canvas id="A6" width="500" height="250px"></canvas>
									</div>
									<div class="col-lg-4">
										<ul style="list-style-type: none;">
											<li><i class="fa fa-square" style="color: #5571F2"></i>
												Feeling in control of telling the recommender what I want</li>
											<li><i class="fa fa-square" style="color: #55C2ED;"></i>
												Don't Feel in control of telling the system what I want</li>
											<li><i class="fa fa-square" style="color: #5CE0C2;"></i>
												Don't feel in control of specifying and changing my
												preferences</li>
											<li><i class="fa fa-square" style="color: #82E895;"></i>
												Understood why the items were recommended to me</li>
											<li><i class="fa fa-square" style="color: #DDEB44;"></i>
												The system helps me understand why the items were
												recommended to me</li>
											<li><i class="fa fa-square" style="color: #D9962B;"></i>
												The system seems to control my decision process rather than
												me</li>
										</ul>
									</div>
								</div>
							</div>
						</div>
						<div class="A7-result row" style="margin-top: 15px">
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A3. Attitudes Evaluation</h3>
								</div>
								<div class="row" style="margin-top: 100px; text-align: center;">
									<div class="col-lg-12">
										<canvas id="A7" width="600px" height="300px"></canvas>
									</div>
								</div>
							</div>
						</div>
						<div class="A8-result row" style="margin-top: 15px">
							<div class="row">
								<div class="col-lg-12 rounded">
									<h3 class="text-center">A4. Behavioral Intentions
										Evaluation</h3>
								</div>
							</div>
							<div class="row" style="margin-top: 25px">
								<div class="col-lg-6">
									<h4 class="rcorner">
										<i class="fa fa-question-circle"></i> If a recommender such as
										this exist, You will use it?
									</h4>
									<div class="col-lg-8">
										<canvas id="A81" style="width: 100% height: 100%"></canvas>
									</div>
									<div class="col-lg-4">
										<ul style="list-style-type: none;">
											<li><i class="fa fa-square" style="color: #F7464A;"></i>
												Yes</li>
											<li><i class="fa fa-square" style="color: #46BFBD;"></i>
												No</li>
										</ul>
									</div>
								</div>
								<div class="col-lg-6">
									<h4 class="rcorner">
										<i class="fa fa-question-circle"></i> Will you continue use
										this Recommender?
									</h4>
									<div class="col-lg-7">
										<canvas id="A82" style="width: 100% height: 100%"></canvas>
									</div>
									<div class="col-lg-5">
										<ul style="list-style-type: none;">
											<li><i class="fa fa-square" style="color: #F7464A;"></i>
												I will use this Recommender again</li>
											<li><i class="fa fa-square" style="color: #46BFBD;"></i>
												I will use this type of Recommender frequently</li>
											<li><i class="fa fa-square" style="color: #FDB45C;"></i>
												I prefer to user this type of Recommender in the future</li>
										</ul>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6 col-md-offset-3">
									<h4 class="rcorner">
										<i class="fa fa-question-circle"></i> You will tell your
										friends about this recommender?
									</h4>
									<div class="col-lg-7">
										<canvas id="A83" style="width: 100% height: 100%"></canvas>
									</div>
									<div class="col-lg-5">
										<ul style="list-style-type: none;">
											<li><i class="fa fa-square" style="color: #F7464A;"></i>
												Yes</li>
											<li><i class="fa fa-square" style="color: #46BFBD;"></i>
												No</li>
										</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- Add Link Modal -->
		<div class="modal fade" id="add-bookmark-modal" tabindex="-1"
			role="dialog" aria-labelledby="addBookmarkModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center" id="addBookmarkModalLabel">
							<i class="fa fa-pencil"></i> Share New Link
						</h4>
						<p class="text-center text-warning">May be the Link you post can not be fetched if its Host Server block or limit connect!</p>
					</div>
					<form role="form" id="add-bookmark-form">
						<div class="modal-body">
							<div class="form-group">
								<label for="new-bookmark-textarea"
									class="control-label text-primary" id="new-bookmark-label">New
									Bookmark:</label>
								<textarea class="form-control" rows="5"
									id="new-bookmark-textarea" placeholder="Url Link"
									style="resize: none"></textarea>
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn btn-success">
								<i class="fa fa-plus" id="add-bookmark-submit"></i> Add Bookmark
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
		<!-- Add Tag Modal -->
		<div class="modal fade" id="add-tag-modal" tabindex="-1" role="dialog"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center">
							<i class="fa fa-tag"></i> Add Tag
						</h4>
					</div>
					<form method="post" role="form" id="add-tag-form"
						action="dashboard/addBookmark">
						<div class="modal-body">
							<div class="form-group">
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<p class="text-info">
											<strong>Url :</strong>
										</p>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-url"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<p class="text-info">
											<strong>Title :</strong>
										</p>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-title"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<p class="text-info">
											<strong> <i class="fa fa-tag"></i>Tags :
											</strong>
										</p>
									</div>
									<div class="col-xs-9">
										<textarea class="form-control" id="bookmark-tags" rows="3"
											placeholder="#tag1   #tag2   #tag3  ...   #tag10" style="resize: none" data-toggle="tooltip"
											data-placement="right"
											data-original-title="Tag are important part of organizing on Social Bookmarking Service. Using tags that are smart and simple benifits both you and community"></textarea>
										<p class="text-info">
											Recommend Tags <i class="fa fa-question"
												data-toggle="tooltip" data-placement="right"
												data-original-title="System use Topic Modeling to recommend tags to user"></i>
										</p>
										<div class="waiting-div"></div>
										<div id="recommend-tags-div"></div>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="form-group">
										<div class="col-xs-3">
											<p class="text-info">
												<i class="fa fa-comment"></i> <strong>Comment : </strong>
											</p>
										</div>
										<div class="col-xs-9">
											<textarea class="form-control" id="bookmark-comment" rows="4"
												placeholder="Your Comment" style="resize: none"
												data-toggle="tooltip" data-placement="right"
												data-original-title="What are you thinking about this Bookmark?"></textarea>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn btn-success">
								<i class="fa fa-plus" id="add-tag-submit"></i> Add Bookmark
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
		<!-- Add Tag vào Bookmark của người dùng khác  -->
		<div class="modal fade" id="nw-add-tag-modal" tabindex="-1"
			role="dialog" aria-labelledby="nwAddTagModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-body">
						<form role="form" id="nw-add-tag-form" class="form-inline">
							<div class="form-group has-feedback">
								<input type="text" id="nw-tag" class="form-control"
									placeholder="Autocomplete Tag or [tag1 tag2 tag3]" style="width: 510px;" />
							</div>
							<button type="submit" class="btn btn-info">Add</button>
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- Edit Tag bookmark của mình  -->
		<div class="modal fade" id="edit-modal" tabindex="-1" role="dialog"
			aria-labelledby="editModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-body">
						<form role="form" id="edit-form" class="form-inline">
							<div class="form-group has-feedback">
								<input type="text" id="edit" class="form-control"
									placeholder="Autocomplete Tag or [tag1, tag2, tag3]" style="width: 480px;" />
							</div>
							<button type="submit" class="btn btn-info">Confirm</button>
							<div id="tagged-tags" style="margin-top: 15px">
								<div style="text-align: center;">
									<i class="fa fa-spinner fa-pulse fa-5x"></i>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- Mở Modal Window Feedback  -->
		<div class="modal fade" id="feedback-modal" tabindex="-1"
			role="dialog" aria-labelledby="feedbackModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center">Send Feedback</h4>
					</div>
					<div class="modal-body">
						<form role="form" id="feedback-form">
							<div class="row">
								<div class="col-xs-2">
									<p class="text-info">
										<strong>Name :</strong>
									</p>
								</div>
								<div class="col-xs-10">
									<p>
										<c:out value="${firstName} ${lastName}" />
									</p>
								</div>
							</div>
							<br />
							<div class="row">
								<div class="col-xs-2">
									<p class="text-info">
										<strong>Problem : </strong>
									</p>
								</div>
								<div class="col-xs-10">
									<textarea class="form-control" id="feedback-area" rows="5"
										placeholder="What problem are you encountering? Which way you want me to improve Website?"
										style="resize: none"></textarea>
								</div>
							</div>
							<br />
							<button type="submit" class="btn btn-info btn-block">Send</button>
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- Subscription tags -->
		<div class="modal fade" id="subscription-modal" tabindex="-1"
			role="dialog" aria-labelledby="subscriptionModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">

					<div class="modal-body">
						<form role="form" id="subscription-form" class="form-inline">
							<div class="form-group has-feedback">
								<input type="text" id="subscription-tag" class="form-control"
									placeholder="Subscription Tags" style="width: 510px;" />
							</div>
							<button type="submit" class="btn btn-info">Add</button>
							<p class="text-center"
								style="margin-top: 15px; padding-bottom: 5px; border-bottom: 1px solid #eee">
								<b>Subscription Tags List :</b>
							</p>
							<div id="subscription-tags-modal" style="margin-top: 15px">
								<div style="text-align: center;">
									<i class="fa fa-spinner fa-pulse fa-2x"></i>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- ========== Xem link trong một iframe modal ========== -->
		<div class="modal fade" id="view-bookmark" tabindex="-1" role="dialog"
			aria-labelledby="viewLinkLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center">Recommend Bookmark</h4>
					</div>
					<div class="modal-body"></div>
				</div>
			</div>
		</div>
		<!-- ========== Survey ========== -->
		<div class="modal fade" id="A8-survey-modal" tabindex="-1"
			role="dialog" aria-labelledby="A8SurveyModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<form role="form" id="A8-survey-form">
					<div class="modal-content">
						<div class="modal-header ">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<div class="modal-title text-center">
								<h3 class="text-primary">Behavioral Intention Survey</h3>
								<small class="text-warning">Please help me finish this
									Survey. This survey plays an important part of my graduate
									research!</small>
							</div>
						</div>
						<div class="modal-body">
							<div class="row">
								<div class="col-lg-12">
									<h4>1. If a recommender such as this exist, You will use
										it to share your Bookmarks</h4>
									<div class="radio">
										<label><input type="radio" name="A81" value="1"
											checked>Yes</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A81" value="0">No</label>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-12">
									<h4>2. Will you continue use this Recommender</h4>
									<div class="radio">
										<label><input type="radio" name="A82" value="1"
											checked>I will use this Recommender again</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A82" value="2">I
											will use this type of Recommender frequently</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A82" value="3">I
											prefer to user this type of Recommender in the future</label>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-12">
									<h4>3. You will tell your friends about this recommender</h4>
									<div class="radio">
										<label><input type="radio" name="A83" value="1"
											checked>Yes</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A83" value="0">No</label>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="submit" class="btn btn-info btn-block">Finish
									Survey</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
		<!-- 	Survey 7   -->
		<div class="modal fade" id="A7-survey-modal" tabindex="-1"
			role="dialog" aria-labelledby="A7SurveyModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<form role="form" id="A7-survey-form">
					<div class="modal-content">
						<div class="modal-header ">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<div class="modal-title text-center">
								<h3 class="text-primary">Attitudes Survey</h3>
								<small class="text-warning">Please help me finish this
									Survey. This survey plays an important part of my graduate
									research!</small>
							</div>
						</div>
						<div class="modal-body">
							<div class="row">
								<div class="col-lg-12">
									<h4>How do you feel about my System?</h4>
									<div class="radio">
										<label><input type="radio" name="A7" value="1" checked>Overall,
											I am satisfied with the recommender.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A7" value="2">I
											am convinced of the items recommended to me</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A7" value="3">I
											am confident I will like the items recommended to me.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A7" value="4">The
											recommender made me more confident</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A7" value="5">The
											recommended items made me confused a bit</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A7" value="6">The
											recommender can be trusted somehow</label>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="submit" class="btn btn-info btn-block">Finish
									Survey</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
		<!-- 	Survey 6   -->
		<div class="modal fade" id="A6-survey-modal" tabindex="-1"
			role="dialog" aria-labelledby="A6SurveyModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<form role="form" id="A6-survey-form">
					<div class="modal-content">
						<div class="modal-header ">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<div class="modal-title text-center">
								<h3 class="text-primary">Control / Transparency Survey</h3>
								<small class="text-warning">Please help me finish this
									Survey. This survey plays an important part of my graduate
									research!</small>
							</div>
						</div>
						<div class="modal-body">
							<div class="row">
								<div class="col-lg-12">
									<h4>How do you feel about my System?</h4>
									<div class="radio">
										<label><input type="radio" name="A6" value="1" checked>I
											feel in control of telling the recommender what I want.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A6" value="2">I
											don't feel in control of telling the system what I want</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A6" value="3">I
											don't feel in control of specifying and changing my
											preferences</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A6" value="4">I
											understood why the items were recommended to me.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A6" value="5">The
											system helps me understand why the items were recommended to
											me.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A6" value="6">The
											system seems to control my decision process rather than me</label>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="submit" class="btn btn-info btn-block">Finish
									Survey</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
		<!-- 	Survey 1   -->
		<div class="modal fade" id="A1-survey-modal" tabindex="-1"
			role="dialog" aria-labelledby="A1SurveyModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<form role="form" id="A1-survey-form">
					<div class="modal-content">
						<div class="modal-header ">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<div class="modal-title text-center">
								<h3 class="text-primary">Quality of Recommended Items
									Survey</h3>
								<small class="text-warning">Please help me finish this
									Survey. This survey plays an important part of my graduate
									research!</small>
							</div>
						</div>
						<div class="modal-body">
							<div class="row">
								<div class="col-lg-12">
									<h4>Accuracy</h4>
									<div class="radio">
										<label><input type="radio" name="A11" value="1"
											checked>The items recommended to me matched my
											interests.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A11" value="2">The
											recommender gave me good suggestions.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A11" value="3">I
											am not interested in the items recommended to me</label>
									</div>
									<h4>Familiarity</h4>
									<div class="radio">
										<label><input type="radio" name="A12" value="1"
											checked>Some of the recommended items are familiar to
											me</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A12" value="2">I
											am not familiar with the items that were recommended to me</label>
									</div>
									<h4>The items recommended to you are attractive?</h4>
									<div class="radio">
										<label><input type="radio" name="A13" value="1"
											checked>Yes</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A13" value="0">No</label>
									</div>
									<h4>Did you enjoy the items recommended to you?</h4>
									<div class="radio">
										<label><input type="radio" name="A14" value="1"
											checked>Yes</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A14" value="0">No</label>
									</div>
									<h4>Novelty</h4>
									<div class="radio">
										<label><input type="radio" name="A15" value="1"
											checked>The items recommended to me are novel and
											interesting.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A15" value="2">The
											recommender system is educational.</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A15" value="3">The
											recommender system helps me discover new contents</label>
									</div>
									<div class="radio">
										<label><input type="radio" name="A15" value="4">I
											could not find new items through the recommender</label>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="submit" class="btn btn-info btn-block">Finish
									Survey</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<script src="js/jquery.js"></script>
	<script src="js/jquery-ui.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/jquery.tagcanvas.min.js"></script>
	<script src="js/jquery.urlive.min.js"></script>
	<script src="js/ScrollMagic.min.js"></script>
	<script src="js/metisMenu.min.js"></script>
	<script src="js/sb-admin-2.js"></script>
	<script src="js/bootbox.js"></script>
	<script src="js/intro.js"></script>
	<script src="js/star-rating.js"></script>
	<script src="js/jquery.lettering.js"></script>
	<script src="js/jquery.textillate.js"></script>
	<script src="js/Chart.js"></script>
	<script src="js/canvasjs.min.js"></script>
	<script src="js/panel.js"></script>

</body>
</html>