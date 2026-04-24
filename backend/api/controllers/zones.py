from flask import jsonify

from dependencies import get_zones_service


@get_zones_service
def get_zones(service):
    try:
        zones = service.get_all()
        return jsonify(zones)
    except Exception as e:
        return jsonify({"error": str(e)}), 500