from psycopg2.extras import RealDictCursor

from repositories.sz_abc_repository import SzRepository


class SzPgRepository(SzRepository):


    def  __init__(self, conn):
        self.conn = conn

    def get_all(self):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute("SELECT * FROM service_zones")
            return cursor.fetchall()