from psycopg2.extras import RealDictCursor

from custom_exceptions import CustomException
from repositories.pt_abc_repository import PtRepository


class PtPgRepository(PtRepository):

    def __init__(self, conn):
        self.conn = conn

    def get_all(self):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute("SELECT * FROM payment_types")
            return cursor.fetchall()

    def get_by_id(self, payment_type_id):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute("SELECT * FROM payment_types WHERE id = %s", (payment_type_id,))
            item = cursor.fetchone()
            if item is None:
                raise CustomException("Payment type not found", 404)

            return item

    def remove_by_id(self, payment_type_id):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            try:
                cursor.execute("DELETE FROM payment_types WHERE id = %s", (payment_type_id,))
                self.conn.commit()
            except Exception as e:
                self.conn.rollback()
                raise e

    def add(self, req_data):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            try:
                cursor.execute("INSERT INTO payment_types(payment_type) VALUES(%s) RETURNING id",
                               (req_data['payment_type'],))
                self.conn.commit()
                return cursor.fetchone()['id']
            except Exception as e:
                self.conn.rollback()
                raise e

    def edit(self, payment_type_id, req_data):
        with self.conn.cursor(cursor_factory=RealDictCursor) as cursor:
            try:
                cursor.execute("UPDATE payment_types SET payment_type = %s WHERE id = %s",
                               (req_data['payment_type'], payment_type_id))
                self.conn.commit()
                return req_data
            except Exception as e:
                self.conn.rollback()
                raise e
