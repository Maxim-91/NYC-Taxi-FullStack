from flask import jsonify

from dependencies import get_boroughs_service


@get_boroughs_service
def get_boroughs(service):
    try:
        boroughs = service.get_all()
        return jsonify(boroughs)
    except Exception as e:
        return jsonify({"error": str(e)}), 500