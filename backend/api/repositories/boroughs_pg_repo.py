from psycopg2.extras import RealDictCursor

from repositories.boroughs_abc_repository import BoroughsRepository


class BoroughsPgRepository(BoroughsRepository):


    def  __init__(self, conn):
        self.conn = conn

    def get_all(self):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute("SELECT * FROM boroughs")
            return cursor.fetchall()



