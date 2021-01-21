package com.company;

import org.postgresql.jdbc.EscapeSyntaxCallMode;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class DBAccessor {
	private String dbname;
	private String host;
	private String port;
	private String user;
	private String passwd;
	private String schema;
	// ESTA ES LA CONEXION
	Connection conn = null;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	public void init() {
		Properties prop = new Properties();
		InputStream propStream = this.getClass().getClassLoader().getResourceAsStream("db.properties");

		try {
			prop.load(propStream);
			this.host = prop.getProperty("host");
			this.port = prop.getProperty("port");
			this.user = prop.getProperty("user");
			this.passwd = prop.getProperty("passwd");
			this.dbname = prop.getProperty("dbname");
			this.schema = prop.getProperty("schema");
		} catch (IOException e) {
			String message = "ERROR: db.properties file could not be found";
			System.err.println(message);
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * Obtains a {@link Connection} to the database, based on the values of the
	 * <code>db.properties</code> file.
	 *
	 * @return DB connection or null if a problem occurred when trying to
	 * connect.
	 */
	public Connection getConnection() {

		// Implement the DB connection
		String url = null;
		try {
			// Loads the driver
			Class.forName("org.postgresql.Driver");

			// Preprara connexió a la base de dades
			StringBuffer sbUrl = new StringBuffer();
			sbUrl.append("jdbc:postgresql:");
			if (host != null && !host.equals("")) {
				sbUrl.append("//").append(host);
				if (port != null && !port.equals("")) {
					sbUrl.append(":").append(port);
				}
			}
			sbUrl.append("/").append(dbname);
			url = sbUrl.toString();

			// Utilitza connexió a la base de dades
			conn = DriverManager.getConnection(url, user, passwd);
			conn.setAutoCommit(false);
		} catch (ClassNotFoundException e1) {
			System.err.println("ERROR: Al Carregar el driver JDBC");
			System.err.println(e1.getMessage());
		} catch (SQLException e2) {
			System.err.println("ERROR: No connectat  a la BD " + url);
			System.err.println(e2.getMessage());
		}

		// Sets the search_path
		if (conn != null) {
			Statement statement = null;
			try {
				statement = conn.createStatement();
				statement.executeUpdate("SET search_path TO " + this.schema);
				// missatge de prova: verificació
				System.out.println("OK: connectat a l'esquema " + this.schema + " de la base de dades ");
				System.out.println();
			} catch (SQLException e) {
				System.err.println("ERROR: Unable to set search_path");
				System.err.println(e.getMessage());
			} finally {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println("ERROR: Closing statement");
					System.err.println(e.getMessage());
				}
			}
		}

		return conn;
	}

	// Crea un jugador
	public void crearJugador() throws SQLException, ParseException {
		Scanner scanner = new Scanner(System.in);

		System.out.println(" CREAR JUGADOR");
		System.out.print("Codigo federacion: ");
		int codigoFederacion = scanner.nextInt();
		scanner.nextLine();
		System.out.print("Primer nombre: ");
		String nombre = scanner.nextLine();
		System.out.print("Segundo nombre: ");
		String apellido = scanner.nextLine();
		System.out.print("Fecha nacimiento (dd/MM/yyyy): ");
		Date fechaNacimiento = simpleDateFormat.parse(scanner.nextLine());
		System.out.print("Genero: ");
		String genero = scanner.nextLine();
		System.out.print("Peso: ");
		String peso = scanner.nextLine();
		try {
			Statement statement = conn.createStatement();
			// INSERTA LOS DATOS QUE HAS PEDIDO PREVIAMENTE
			statement.executeUpdate("INSERT INTO player values ( '"+nombre+"', '"+apellido+"', '"+fechaNacimiento+"', '"+genero+"', '"+peso+"' )");
			conn.commit();
			statement.close();
			System.out.println("Datos insertados con exito.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR!");
		}
	}

	// Crea un partido
	public void crearPartit() throws SQLException, ParseException {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Equipo local: ");
		String equipoLocal = scanner.nextLine();
		// Compruebo si existe
		System.out.print("Equipo visitante: ");
		String equipoVisitante = scanner.nextLine();
		// Compruebo si existe
		System.out.print("Fecha del partido (dd/MM/yyyy): ");
		Date fechaPartido = simpleDateFormat.parse(scanner.nextLine());
		System.out.print("Asistencia: ");
		int asistencia = scanner.nextInt();
		scanner.nextLine();
		System.out.print("MVP Player: ");
		String mvpPlayer = scanner.nextLine();

		try {
			Statement statement = conn.createStatement();
			// INSERTA LOS DATOS QUE HAS PEDIDO PREVIAMENTE
			statement.executeUpdate("INSERT INTO match values ( '"+equipoLocal+"', '"+equipoVisitante+"', '"+fechaPartido+"', '"+asistencia+"', '"+mvpPlayer+"' )");
			conn.commit();
			statement.close();
			System.out.println("Datos insertados con exito.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR!");
		}
	}

	// Muestra
	public void mostrarJugadorSinEquipo() throws SQLException {
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM player where team_name is null");
		// Siempre que hayan datos
		while (resultSet.next()) {
			System.out.println(resultSet.getString("federation_license_code") + " " + resultSet.getString("first_name") + " " + resultSet.getString("last_name") + " " + resultSet.getString("birth_date") + " " + resultSet.getString("gender") + " " + resultSet.getString("height") + " " + resultSet.getString("team_name") + " " + resultSet.getString("mvp_total"));
		}
		statement.close();
	}

	// Desvincula a un jugador de un equipo
	public void desvincularJugador() {
		Scanner scanner = new Scanner(System.in);
		try {
			Statement statement = conn.createStatement();
			System.out.print("Escribe el federation_license_code del jugador: ");
			String codigoJugador = scanner.nextLine();
				statement.executeUpdate("UPDATE player set team_name = " + null + " where federation_license_code = '" + codigoJugador + "'");
				// DESPUES DE CADA MODIFICACION
				conn.commit();
				statement.close();
//				ResultSet resultSet = statement.executeQuery("SELECT * from player where federation_license_code = '"+codigoJugador+"' ");
				/*String equipo = null;
				if (resultSet.next()) {
					equipo = resultSet.getString("team_name");
					// Siempre que tenga un equipo asignado
					if (equipo != null) {
						System.out.print("Estas seguro que deseas desvincular a este jugador del equipo " + equipo + "? (S/N): ");
						if (scanner.nextLine().toLowerCase().equals("s")) {
							resultSet.updateString("team_name", null);
							resultSet.updateRow();
							conn.commit();
						} else {
							System.out.println("Jugador no desvinculado.");
						}
					} else {
						System.out.println("Este jugador no tiene ningun equipo asignado.");
					}
				}*/
			} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR!");
		}
	}

	// Asigna el jugador a un equipo
	public void asignarJugador() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		Statement statement = null;
		try {
			statement = conn.createStatement();
			System.out.print("Escribe el federation_license_code del jugador: ");
			String codigoJugador = scanner.nextLine();
			System.out.print("Escribe el nombre del equipo para este jugador: ");
			String equipo = scanner.nextLine();
				statement.executeUpdate("UPDATE player set team_name =  '" + equipo + "' where federation_license_code = '" + codigoJugador + "' ");
				conn.commit();
				System.out.println("Datos actualizados.");
			} catch (SQLException e) {
			System.out.println("ERROR!");
		}
		statement.close();
	}


	// Carga las estadisticas
	public void carregaEstadistiques() throws IOException, SQLException {
		File file = new File(System.getProperty("user.home") + "/Escritorio/estadistiques.csv");
		BufferedReader br = new BufferedReader(new FileReader(file));
		br.readLine();
		String linea = br.readLine();
		Statement statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		while (linea != null) {
			try {
				PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO match_statistics VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				String[] datos = linea.split(",");

				final String OLD_FORMAT = "dd/MM/yyyy";
				final String NEW_FORMAT = "yyyy-MM-dd";
				String oldDateString = datos[2];
				SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
				Date d = sdf.parse(oldDateString);
				sdf.applyPattern(NEW_FORMAT);
				String fecha = sdf.format(d);

				ResultSet resultSet = statement.executeQuery("SELECT * FROM match_statistics where home_team = '" + datos[0] + "' AND " +
						"visitor_team = '" + datos[1] + "' AND match_date = '" + fecha + "'AND player = '" + datos[3] + "'");
				boolean existe = false;
				while (resultSet.next()) {
					existe = true;
					resultSet.updateString(1, datos[0]);
					resultSet.updateString(2, datos[1]);
					resultSet.updateDate(3, java.sql.Date.valueOf(fecha));
					resultSet.updateString(4, datos[3]);
					for (int i = 5; i < datos.length + 1; i++) {
						resultSet.updateInt(i, Integer.parseInt(datos[i - 1]));
					}
					resultSet.updateRow();
					System.out.println("Datos modificados con exito");
				}

				if (!existe) {
					preparedStatement.clearParameters();
					preparedStatement.setString(1, datos[0]);
					preparedStatement.setString(2, datos[1]);
					preparedStatement.setDate(3, java.sql.Date.valueOf(fecha));
					preparedStatement.setString(4, datos[3]);
					for (int i = 5; i < datos.length + 1; i++) {
						preparedStatement.setInt(i, Integer.parseInt(datos[i - 1]));
					}
					preparedStatement.executeUpdate();
					System.out.println("Datos insertados con exito");
				}
				resultSet.close();
				preparedStatement.close();
				conn.commit();
			} catch (SQLException throwables) {
				conn.rollback();
				throwables.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			linea = br.readLine();
		}
		statement.close();
	}


	public void mostrarEquips() throws SQLException {

		Statement statement = conn.createStatement();
		ResultSet resultSet = null;
		// La QUERY
		resultSet = statement.executeQuery("SELECT * FROM team");
		// Por cada linea de respuesta
		while (resultSet.next()) {
			System.out.println(resultSet.getString("name") + " " + resultSet.getString("type"));
		}
		resultSet.close();
		statement.close();

	}

	public void mostrarJugador() throws SQLException {
		Statement statement = conn.createStatement();
		ResultSet resultSet = null;
		// La QUERY
		resultSet = statement.executeQuery("SELECT * FROM player");
		// Por cada linea de respuesta
		while (resultSet.next()) {
			System.out.println(resultSet.getString("federation_license_code") + " " + resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
		}
		resultSet.close();
		statement.close();

	}

	public void crearEquipo() throws SQLException {
		Scanner scanner = new Scanner(System.in);

		System.out.println(" CREAR EQUIPO");
		System.out.println("--------------");
		System.out.print("Nombre: ");
		String nombre = scanner.nextLine();
		System.out.print("Tipo: ");
		String tipo = scanner.nextLine();
		System.out.print("Pais: ");
		String pais = scanner.nextLine();
		System.out.print("Ciudad: ");
		String ciudad = scanner.nextLine();
		System.out.print("Nombre corto: ");
		String nombreCorto = scanner.nextLine();

		try {
			Statement statement = conn.createStatement();
			// INSERTA LOS DATOS QUE HAS PEDIDO PREVIAMENTE
			statement.executeUpdate("INSERT INTO team values ( '" + nombre + "', '" + tipo + "', '" + pais + "', '" + ciudad + "', '" + nombreCorto + "' )");
			conn.commit();
			statement.close();
			System.out.println("Datos insertados con exito.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR!");
		}
	}

	public void sortir() throws SQLException {
		System.out.println("ADÉU!");
		conn.close();
		System.exit(0);
	}
}

