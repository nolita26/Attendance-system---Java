import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

class Subject 
{
	int attended, total;

	Subject(int attended, int total) 
	{
		this.attended = attended;
		this.total = total;
	}
}

class Student 
{
	String name;
	int rollNo;
	float marks;
	Subject subjects[] = new Subject[5];

	Student(String name, int rollNo, float marks) 
	{
		this.name = name;
		this.rollNo = rollNo;
		this.marks = marks;
	}

	void setSubjects() 
	{
		Scanner sc = new Scanner(System.in);
		for (int i = 1; i <= 5; i++) {
			System.out.println("Enter no. of attended lectures for Subject " + i);
			int attended = sc.nextInt();
			System.out.println("Enter no. of total lectures for Subject " + i);
			int total = sc.nextInt();
			this.subjects[i - 1] = new Subject(attended, total);
		}
	}
}

class DBStudent extends Student 
{
	float sub1, sub2, sub3, sub4, sub5, total;

	DBStudent(String name, int rollNo, float marks, float sub1, float sub2, float sub3, float sub4, float sub5, float total) 
	{
		super(name, rollNo, marks);
		this.sub1 = sub1;
		this.sub2 = sub2;
		this.sub3 = sub3;
		this.sub4 = sub4;
		this.sub5 = sub5;
		this.total = total;
	}

	void printResult() 
	{
		if (this.total >= 75) 
		{
			System.out.println(this.name + " -- ELIGIBLE");
		}
		else if (this.total < 75 && this.total >= 40) 
		{
			if (this.marks >= 75) 
			{
				System.out.println(this.name + " -- ELIGIBLE");
			} 
			else 
			{
				System.out.println(this.name + " -- NOT ELIGIBLE");
			}
		} 
		else 
		{
			System.out.println(this.name + " -- NOT ELIGIBLE");
		}
	}

	void printDetails() 
	{
		System.out.println("NAME: " + this.name);
		System.out.println("ROLL NO: " + this.rollNo);
		System.out.println("SUBJECT 1: " + this.sub1 * 100);
		System.out.println("SUBJECT 2: " + this.sub2 * 100);
		System.out.println("SUBJECT 3: " + this.sub3 * 100);
		System.out.println("SUBJECT 4: " + this.sub4 * 100);
		System.out.println("SUBJECT 5: " + this.sub5 * 100);
		System.out.println("UT 1 MARKS: " + this.marks);
		System.out.println("TOTAL ATTENDANCE: " + this.total);

		if (this.total >= 75)
		{
			System.out.println("YOU ARE ELIGIBLE!");
		}
		else if (this.total < 75 && this.total >= 40) 
		{
			if (this.marks >= 75) 
			{
				System.out.println("YOU ARE ELIGIBLE");
			}
			else 
			{
				System.out.println("YOU ARE NOT ELIGIBLE");
			}
		} 
		else 
		{
			System.out.println("YOU ARE NOT ELIGIBLE");
		}
	}
}

class Database 
{
	String DRIVER = "com.mysql.jdbc.Driver";
	String URL, USERNAME, PASSWORD;
	Connection conn = null;

	Database(String URL, String USERNAME, String PASSWORD) 
	{
		this.URL = URL;
		this.USERNAME = USERNAME;
		this.PASSWORD = PASSWORD;
	}

	void connect() 
	{
		try 
		{
			Class.forName(this.DRIVER);
			System.out.println("Connecting To A Database .....");
			this.conn = DriverManager.getConnection(this.URL, this.USERNAME, this.PASSWORD);
			System.out.println("Connected!");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}

public class AttendanceSys 
{
	public static void main(String args[]) 
	{
		Scanner sc = new Scanner(System.in);
		Database db = new Database("jdbc:mysql://localhost/attendance", "root", "");
		db.connect();
		int choice;
		while (true) 
		{
			System.out.println("**********WELCOME TO STUDENT HUB**********");
			System.out.println("WHAT WOULD YOU LIKE TO DO?\n1. Add Student Data\n2. View Student Data\n3. View All Data\n4. Exit");
			choice = sc.nextInt();
			switch (choice) 
			{
				case 1: System.out.println("Name of the Student:");
						String name = sc.next();
						System.out.println("Roll Number of the Student:");
						int rollNo = sc.nextInt();
						System.out.println("UT 1 Marks of the Student:");
						float marks = sc.nextFloat();
						Student s = new Student(name, rollNo, marks);
						s.setSubjects();
						float attended = 0;
						float totalLect = 0;
						try 
						{
							PreparedStatement stmt = db.conn.prepareStatement("INSERT INTO `students`(name,rollNo,marks,s1,s1total,s2,s2total,s3,s3total,s4,s4total,s5,s5total,total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
							stmt.setString(1, s.name);
							stmt.setInt(2, s.rollNo);
							stmt.setFloat(3, s.marks);
							for (int i = 0, j = 4; i < 5 && j <= 12; i++, j = j + 2) {
							stmt.setInt(j, s.subjects[i].attended);
							attended += s.subjects[i].attended;
							stmt.setInt(j + 1, s.subjects[i].total);
							totalLect += s.subjects[i].total;
						}
						float total = attended / totalLect * 100;
						stmt.setFloat(14, total);
						stmt.execute(); 
						catch (SQLException e) 
						{
							e.printStackTrace();
						}
						System.out.println("Student Added!");
						break;
				case 2: System.out.println("Enter Roll Number of the Student:");
						int sRoll = sc.nextInt();
						try 
						{
							PreparedStatement stmt = db.conn.prepareStatement("SELECT * FROM `students` WHERE `rollNo`=?");
							stmt.setInt(1, sRoll);
							ResultSet rs = stmt.executeQuery();
							while (rs.next()) 
							{
								String sname = rs.getString(1);
								int sno = rs.getInt(2);
								float smarks = rs.getFloat(3);
								float stotal = rs.getFloat(14);
								float s1 = rs.getFloat(4) / rs.getFloat(5);
								float s2 = rs.getFloat(6) / rs.getFloat(7);
								float s3 = rs.getFloat(8) / rs.getFloat(9);
								float s4 = rs.getFloat(10) / rs.getFloat(11);
								float s5 = rs.getFloat(12) / rs.getFloat(13);
								DBStudent ds = new DBStudent(sname, sno, smarks, s1, s2, s3, s4, s5, stotal);
								ds.printDetails();
							}
						} 
						catch (SQLException e) 
						{
							e.printStackTrace();
						}
						break;
				case 3: System.out.println("LIST OF STUDENTS:");
						try 
						{
							PreparedStatement stmt = db.conn.prepareStatement("SELECT * FROM `students`");
							ResultSet rs = stmt.executeQuery();
							while (rs.next()) 
							{
								String sname = rs.getString(1);
								int sno = rs.getInt(2);
								float smarks = rs.getFloat(3);
								float stotal = rs.getFloat(14);
								float s1 = rs.getFloat(4) / rs.getFloat(5);
								float s2 = rs.getFloat(6) / rs.getFloat(7);
								float s3 = rs.getFloat(8) / rs.getFloat(8);
								float s4 = rs.getFloat(10) / rs.getFloat(11);
								float s5 = rs.getFloat(12) / rs.getFloat(13);
								DBStudent ds = new DBStudent(sname, sno, smarks, s1, s2, s3, s4, s5, stotal);
								ds.printResult();
							}
						} 
						catch (SQLException e) 
						{
							e.printStackTrace();
						}
						break;
				case 4: sc.close();
						System.exit(0);
						break;
				default:System.out.println("INVALID CHOICE!");
			}
		}
	}
}
	
/*Output:
	Connecting To A Database .....

	Connected!
	**********WELCOME TO STUDENT HUB**********
	WHAT WOULD YOU LIKE TO DO?
	1. Add Student Data
	2. View Student Data
	3. View All Data
	4. Exit
	1
	Name of the Student:
	Benita
	Roll Number of the Student:
	8362
	UT 1 Marks of the Student:
	67
	Enter no. of attended lectures for Subject 1
	17
	Enter no. of total lectures for Subject 1
	20
	Enter no. of attended lectures for Subject 2
	18
	Enter no. of total lectures for Subject 2
	20
	Enter no. of attended lectures for Subject 3
	17
	Enter no. of total lectures for Subject 3
	20
	Enter no. of attended lectures for Subject 4
	18
	Enter no. of total lectures for Subject 4
	20
	Enter no. of attended lectures for Subject 5
	17
	Enter no. of total lectures for Subject 5
	20
	Student Added!
	**********WELCOME TO STUDENT HUB**********
	WHAT WOULD YOU LIKE TO DO?
	1. Add Student Data
	2. View Student Data
	3. View All Data
	4. Exit
	1
	Name of the Student:
	Nolita
	Roll Number of the Student:
	8363
	UT 1 Marks of the Student:
	58
	Enter no. of attended lectures for Subject 1
	19
	Enter no. of total lectures for Subject 1
	20
	Enter no. of attended lectures for Subject 2
	18
	Enter no. of total lectures for Subject 2
	20
	Enter no. of attended lectures for Subject 3
	18
	Enter no. of total lectures for Subject 3
	20
	Enter no. of attended lectures for Subject 4
	17
	Enter no. of total lectures for Subject 4
	20
	Enter no. of attended lectures for Subject 5
	18
	Enter no. of total lectures for Subject 5
	20
	Student Added!
	**********WELCOME TO STUDENT HUB**********
	WHAT WOULD YOU LIKE TO DO?
	1. Add Student Data
	2. View Student Data
	3. View All Data
	4. Exit
	2
	Enter Roll Number of the Student:
	8362
	NAME: Benita
	ROLL NO: 8362
	SUBJECT 1: 85.0
	SUBJECT 2: 90.0
	SUBJECT 3: 85.0
	SUBJECT 4: 90.0
	SUBJECT 5: 85.0
	UT 1 MARKS: 67.0
	TOTAL ATTENDANCE: 87.0
	YOU ARE ELIGIBLE!
	**********WELCOME TO STUDENT HUB**********
	WHAT WOULD YOU LIKE TO DO?
	1. Add Student Data
	2. View Student Data
	3. View All Data
	4. Exit
	2
	Enter Roll Number of the Student:
	8363
	NAME: Nolita
	ROLL NO: 8363
	SUBJECT 1: 95.0
	SUBJECT 2: 90.0
	SUBJECT 3: 90.0
	SUBJECT 4: 85.0
	SUBJECT 5: 90.0
	UT 1 MARKS: 58.0
	TOTAL ATTENDANCE: 90.0
	YOU ARE ELIGIBLE!
	**********WELCOME TO STUDENT HUB**********
	WHAT WOULD YOU LIKE TO DO?
	1. Add Student Data
	2. View Student Data
	3. View All Data
	4. Exit
	3
	LIST OF STUDENTS:
	Benita -- ELIGIBLE
	Nolita -- ELIGIBLE
	**********WELCOME TO STUDENT HUB**********
	WHAT WOULD YOU LIKE TO DO?
	1. Add Student Data
	2. View Student Data
	3. View All Data
	4. Exit
*/