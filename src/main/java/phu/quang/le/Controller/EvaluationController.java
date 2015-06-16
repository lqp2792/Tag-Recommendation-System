package phu.quang.le.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import phu.quang.le.Model.A1;
import phu.quang.le.Model.A6;
import phu.quang.le.Model.A7;
import phu.quang.le.Model.A8;
import phu.quang.le.Model.JsonResponse;
import phu.quang.le.Utility.DBUtility;

@Controller
@RequestMapping(value = "/evaluation")
public class EvaluationController {
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getNetworkView(HttpSession session) {
		if (session.getAttribute("userID") == null) {
			return new ModelAndView("redirect:/");
		} else {
			if (session.getAttribute("sortBy") == null) {
				session.setAttribute("sortBy", 5);
			}
			;
			ModelAndView dashboard = new ModelAndView("dashboard");
			dashboard.addObject("firstName", session.getAttribute("firstName"));
			dashboard.addObject("lastName", session.getAttribute("lastName"));

			return dashboard;
		}
	}

	@RequestMapping(value = "/clickMenu", method = RequestMethod.POST)
	public @ResponseBody JsonResponse surveyCondition(HttpSession session,
			@RequestParam int menuID) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		processClick(userID, menuID);
		return result;
	}

	/* ======================================================== */
	/* Process condition show Survey */
	/* ======================================================== */
	@RequestMapping(value = "/showA8", method = RequestMethod.GET)
	public @ResponseBody JsonResponse checkShowA8(HttpSession session) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		boolean rs = processCheckShowA8(userID);
		if (rs) {
			result.setStatus("SUCCESS");
		} else {
			result.setStatus("FAIL");
		}
		return result;
	}

	@RequestMapping(value = "/showA7", method = RequestMethod.GET)
	public @ResponseBody JsonResponse checkShowA7(HttpSession session) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		boolean rs = processCheckShowA7(userID);
		if (rs) {
			result.setStatus("SUCCESS");
		} else {
			result.setStatus("FAIL");
		}
		return result;
	}

	@RequestMapping(value = "/showA6", method = RequestMethod.GET)
	public @ResponseBody JsonResponse checkShowA6(HttpSession session) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		boolean rs = processCheckShowA6(userID);
		if (rs) {
			result.setStatus("SUCCESS");
		} else {
			result.setStatus("FAIL");
		}
		return result;
	}

	@RequestMapping(value = "/showA1", method = RequestMethod.GET)
	public @ResponseBody JsonResponse checkShowA1(HttpSession session) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		boolean rs = processCheckShowA1(userID);
		if (rs) {
			result.setStatus("SUCCESS");
		} else {
			result.setStatus("FAIL");
		}
		return result;
	}

	/* ======================================================== */
	/* Process submit Survey */
	/* ======================================================== */
	@RequestMapping(value = "/submitA8", method = RequestMethod.POST)
	public @ResponseBody JsonResponse submitA8(HttpSession session,
			@RequestParam int A81, @RequestParam int A82, @RequestParam int A83) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		int rs = processSubmitA8(userID, A81, A82, A83);
		if (rs > 0) {
			result.setStatus("SUCCESS");
			result.setResult("Thank You for your time in contributing to this evaluation");
		} else {
			result.setStatus("Something wrong has happend ");
		}
		return result;
	}

	@RequestMapping(value = "/submitA7", method = RequestMethod.POST)
	public @ResponseBody JsonResponse submitA7(HttpSession session,
			@RequestParam int A7) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		int rs = processSubmitA7(userID, A7);
		if (rs > 0) {
			result.setStatus("SUCCESS");
			result.setResult("Thank You for your time in contributing to this evaluation");
		} else {
			result.setStatus("Something wrong has happend ");
		}
		return result;
	}

	@RequestMapping(value = "/submitA6", method = RequestMethod.POST)
	public @ResponseBody JsonResponse submitA6(HttpSession session,
			@RequestParam int A6) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		int rs = processSubmitA6(userID, A6);
		if (rs > 0) {
			result.setStatus("SUCCESS");
			result.setResult("Thank You for your time in contributing to this evaluation");
		} else {
			result.setStatus("Something wrong has happend ");
		}
		return result;
	}

	@RequestMapping(value = "/submitA1", method = RequestMethod.POST)
	public @ResponseBody JsonResponse submitA1(HttpSession session,
			@RequestParam int A11, @RequestParam int A12,
			@RequestParam int A13, @RequestParam int A14, @RequestParam int A15) {
		JsonResponse result = new JsonResponse();
		int userID = (int) session.getAttribute("userID");
		int rs = processSubmitA1(userID, A11, A12, A13, A14, A15);
		if (rs > 0) {
			result.setStatus("SUCCESS");
			result.setResult("Thank You for your time in contributing to this evaluation");
		} else {
			result.setStatus("Something wrong has happend ");
		}
		return result;
	}

	/* ======================================================== */
	/* Process Get Survey Result */
	/* ======================================================== */
	@RequestMapping(value = "/getA8Result", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getA8Result(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		A8 A8Result = processGetA8Result();
		rs.setResult(A8Result);
		return rs;
	}

	@RequestMapping(value = "/getA7Result", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getA7Result(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		A7 A7Result = processGetA7Result();
		rs.setResult(A7Result);
		return rs;
	}

	@RequestMapping(value = "/getA6Result", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getA6Result(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		A6 A6Result = processGetA6Result();
		rs.setResult(A6Result);
		return rs;
	}

	@RequestMapping(value = "/getA1Result", method = RequestMethod.GET)
	public @ResponseBody JsonResponse getA1Result(HttpSession session) {
		JsonResponse rs = new JsonResponse();
		A1 A1Result = processGetA1Result();
		rs.setResult(A1Result);
		return rs;
	}

	/* ======================================================== */
	/* Support Function */
	/* ======================================================== */
	public void processClick(int userID, int menuID) {
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * FROM sv_condition WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				switch (menuID) {
				case 1:
					sql = "UPDATE sv_condition SET click_network = click_network + 1 WHERE userID = ?";
					break;
				case 2:
					sql = "UPDATE sv_condition SET click_discover = click_network + 1 WHERE userID = ?";
					break;
				case 3:
					sql = "UPDATE sv_condition SET click_trending = click_network + 1 WHERE userID = ?";
					break;
				}
				PreparedStatement pst1 = c.prepareStatement(sql);
				pst1.setInt(1, userID);
				pst1.executeUpdate();
			} else {
				switch (menuID) {
				case 1:
					sql = "INSERT INTO sv_condition VALUES (?, ?, default, default, default)";
					break;
				case 2:
					sql = "INSERT INTO sv_condition VALUES (?, default, default, ?, default)";
					break;
				case 3:
					sql = "INSERT INTO sv_condition VALUES (?, default, ?, default, default)";
					break;
				}
				PreparedStatement pst1 = c.prepareStatement(sql);
				pst1.setInt(1, userID);
				pst1.setInt(2, 1);
				pst1.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
	}

	public boolean processCheckShowA8(int userID) {
		boolean isReady = false;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * from sv_condition WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				int mark = rs.getInt(5);
				int networkOK = rs.getInt(2) - mark;
				int trendingOK = rs.getInt(3) - mark;
				int discoverOK = rs.getInt(4) - mark;

				if (networkOK >= 1 && trendingOK >= 1 && discoverOK >= 1) {
					sql = "SELECT * from sv_a8 WHERE userID = ? ORDER BY survey_time DESC LIMIT 1";
					pst = c.prepareStatement(sql);
					pst.setInt(1, userID);
					ResultSet rs1 = pst.executeQuery();
					if (rs1.next()) {
						Date currentDate = new Date();
						long diff = currentDate.getTime()
								- rs1.getDate(5).getTime();
						int betweenDays = (int) TimeUnit.DAYS.convert(diff,
								TimeUnit.MILLISECONDS);
						if (betweenDays >= 1) {
							isReady = true;
						}
					} else {
						isReady = true;
					}
				}
			}
			// is ready + mark len 2
			if (isReady) {
				sql = "UPDATE sv_condition SET show_behavior_mark = show_behavior_mark + 2 WHERE userID = ?";
				pst = c.prepareStatement(sql);
				pst.setInt(1, userID);
				pst.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return isReady;
	}

	public boolean processCheckShowA7(int userID) {
		boolean isReady = true;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * from sv_condition WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				int networkClick = rs.getInt(2);
				int discoverClick = rs.getInt(4);
				if (networkClick == 0 && discoverClick == 0) {
					isReady = false;
				}
				if ((networkClick % 3) == 0 || (discoverClick % 3) == 0) {
					sql = "SELECT * from sv_a7 WHERE userID = ? ORDER BY survey_time DESC LIMIT 1";
					pst = c.prepareStatement(sql);
					pst.setInt(1, userID);
					ResultSet rs1 = pst.executeQuery();
					if (rs1.next()) {
						Date currentDate = new Date();
						long diff = currentDate.getTime()
								- rs1.getDate(3).getTime();
						int betweenDays = (int) TimeUnit.DAYS.convert(diff,
								TimeUnit.MILLISECONDS);
						if (betweenDays < 1) {
							isReady = false;
						}
					}
				} else {
					System.out.println("Not show A7");
					isReady = false;
				}
				sql = "SELECT count(userID) from bookmarks_new WHERE userID = ? ";
				pst = c.prepareStatement(sql);
				pst.setInt(1, userID);
				ResultSet rs1 = pst.executeQuery();
				if (rs1.next()) {
					if (rs1.getInt(1) < 3) {
						isReady = false;
					}
				}
			} else {
				isReady = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return isReady;
	}

	public boolean processCheckShowA6(int userID) {
		boolean isReady = true;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * from sv_condition WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				int networkClick = rs.getInt(2);
				int discoverClick = rs.getInt(4);
				if (networkClick == 0 && discoverClick == 0) {
					isReady = false;
				}
				if ((networkClick % 5) == 0 || (discoverClick % 5) == 0) {
					sql = "SELECT * from sv_a6 WHERE userID = ? ORDER BY survey_time DESC LIMIT 1";
					pst = c.prepareStatement(sql);
					pst.setInt(1, userID);
					ResultSet rs1 = pst.executeQuery();
					if (rs1.next()) {
						Date currentDate = new Date();
						long diff = currentDate.getTime()
								- rs1.getDate(3).getTime();
						int betweenDays = (int) TimeUnit.DAYS.convert(diff,
								TimeUnit.MILLISECONDS);
						if (betweenDays < 1) {
							isReady = false;
						}
					}
				} else {
					isReady = false;
				}
				sql = "SELECT count(userID) from bookmarks_new WHERE userID = ? ";
				pst = c.prepareStatement(sql);
				pst.setInt(1, userID);
				ResultSet rs1 = pst.executeQuery();
				if (rs1.next()) {
					if (rs1.getInt(1) < 2) {
						isReady = false;
					}
				}
			} else {
				isReady = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return isReady;
	}

	public boolean processCheckShowA1(int userID) {
		boolean isReady = true;
		Connection c = DBUtility.getConnection();
		String sql = "SELECT * from sv_condition WHERE userID = ?";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				int networkClick = rs.getInt(2);
				int discoverClick = rs.getInt(4);
				if (networkClick == 0 && discoverClick == 0) {
					isReady = false;
				}
				if ((networkClick % 7) == 0 || (discoverClick % 7) == 0) {
					sql = "SELECT * from sv_a1 WHERE userID = ? ORDER BY survey_time DESC LIMIT 1";
					pst = c.prepareStatement(sql);
					pst.setInt(1, userID);
					ResultSet rs1 = pst.executeQuery();
					if (rs1.next()) {
						Date currentDate = new Date();
						long diff = currentDate.getTime()
								- rs1.getDate(7).getTime();
						int betweenDays = (int) TimeUnit.DAYS.convert(diff,
								TimeUnit.MILLISECONDS);
						if (betweenDays < 1) {
							isReady = false;
						}
					}
				} else {
					isReady = false;
				}
				sql = "SELECT count(userID) from bookmarks_new WHERE userID = ? ";
				pst = c.prepareStatement(sql);
				pst.setInt(1, userID);
				ResultSet rs1 = pst.executeQuery();
				if (rs1.next()) {
					if (rs1.getInt(1) < 4) {
						isReady = false;
					}
				}
			} else {
				isReady = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return isReady;
	}

	public int processSubmitA8(int userID, int A81, int A82, int A83) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO sv_a8 VALUES (?, ?, ?, ?, default)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, A81);
			pst.setInt(3, A82);
			pst.setInt(4, A83);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return result;
	}

	public int processSubmitA7(int userID, int A7) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO sv_a7 VALUES (?, ?, default)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, A7);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return result;
	}

	public int processSubmitA6(int userID, int A6) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO sv_a6 VALUES (?, ?, default)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, A6);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return result;
	}

	public int processSubmitA1(int userID, int A11, int A12, int A13, int A14,
			int A15) {
		int result = -1;
		Connection c = DBUtility.getConnection();
		String sql = "INSERT INTO sv_a1 VALUES (?, ?, ?, ?, ?, ?, default)";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, userID);
			pst.setInt(2, A11);
			pst.setInt(3, A12);
			pst.setInt(4, A13);
			pst.setInt(5, A14);
			pst.setInt(6, A15);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}
		return result;
	}

	public A8 processGetA8Result() {
		A8 A8Result = new A8();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT A81, COUNT(A81) FROM sv_a8 GROUP BY A81";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			rs.next();
			A8Result.setA81N(rs.getInt(2));
			rs.next();
			A8Result.setA81Y(rs.getInt(2));
			sql = "SELECT A82, COUNT(A82) FROM sv_a8 GROUP BY A82";
			pst = c.prepareStatement(sql);
			rs = pst.executeQuery();
			rs.next();
			A8Result.setA821(rs.getInt(2));
			rs.next();
			A8Result.setA822(rs.getInt(2));
			rs.next();
			A8Result.setA823(rs.getInt(2));
			sql = "SELECT A83, COUNT(A83) FROM sv_a8 GROUP BY A83";
			pst = c.prepareStatement(sql);
			rs = pst.executeQuery();
			rs.next();
			A8Result.setA83N(rs.getInt(2));
			rs.next();
			A8Result.setA83Y(rs.getInt(2));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return A8Result;
	}

	public A7 processGetA7Result() {
		A7 A7Result = new A7();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT A7, COUNT(A7) FROM sv_a7 GROUP BY A7";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			rs.next();
			A7Result.setA71(rs.getInt(2));
			rs.next();
			A7Result.setA72(rs.getInt(2));
			rs.next();
			A7Result.setA73(rs.getInt(2));
			rs.next();
			A7Result.setA74(rs.getInt(2));
			rs.next();
			A7Result.setA75(rs.getInt(2));
			rs.next();
			A7Result.setA76(rs.getInt(2));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return A7Result;
	}

	public A6 processGetA6Result() {
		A6 A6Result = new A6();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT A6, COUNT(A6) FROM sv_a6 GROUP BY A6";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			rs.next();
			A6Result.setA61(rs.getInt(2));
			rs.next();
			A6Result.setA62(rs.getInt(2));
			rs.next();
			A6Result.setA63(rs.getInt(2));
			rs.next();
			A6Result.setA64(rs.getInt(2));
			rs.next();
			A6Result.setA65(rs.getInt(2));
			rs.next();
			A6Result.setA66(rs.getInt(2));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return A6Result;
	}

	public A1 processGetA1Result() {
		A1 A1Result = new A1();
		Connection c = DBUtility.getConnection();
		String sql = "SELECT A11, COUNT(A11) FROM sv_a1 GROUP BY A11";
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			rs.next();
			A1Result.setA111(rs.getInt(2));
			rs.next();
			A1Result.setA112(rs.getInt(2));
			rs.next();
			A1Result.setA113(rs.getInt(2));
			sql = "SELECT A12, COUNT(A12) FROM sv_a1 GROUP BY A12";
			pst = c.prepareStatement(sql);
			rs = pst.executeQuery();
			rs.next();
			A1Result.setA121(rs.getInt(2));
			rs.next();
			A1Result.setA122(rs.getInt(2));
			sql = "SELECT A13, COUNT(A13) FROM sv_a1 GROUP BY A13";
			pst = c.prepareStatement(sql);
			rs = pst.executeQuery();
			rs.next();
			A1Result.setA13N(rs.getInt(2));
			rs.next();
			A1Result.setA13Y(rs.getInt(2));
			sql = "SELECT A14, COUNT(A14) FROM sv_a1 GROUP BY A14";
			pst = c.prepareStatement(sql);
			rs = pst.executeQuery();
			rs.next();
			A1Result.setA14N(rs.getInt(2));
			rs.next();
			A1Result.setA14Y(rs.getInt(2));
			sql = "SELECT A15, COUNT(A15) FROM sv_a1 GROUP BY A15";
			pst = c.prepareStatement(sql);
			rs = pst.executeQuery();
			rs.next();
			A1Result.setA151(rs.getInt(2));
			rs.next();
			A1Result.setA152(rs.getInt(2));
			rs.next();
			A1Result.setA153(rs.getInt(2));
			rs.next();
			A1Result.setA154(rs.getInt(2));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(c);
		}

		return A1Result;
	}
}
