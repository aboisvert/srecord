package srecord.jdbc

import java.sql.Connection

trait ConnectionProvider {
  def connection: Connection
}
