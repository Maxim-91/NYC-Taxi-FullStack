from flask import jsonify

from dependencies import get_vendors_service


@get_vendors_service
def get_vendors(service):
    try:
        vendors = service.get_all()
        return jsonify(vendors)
    except Exception as e:
        return jsonify({"error": str(e)}), 500