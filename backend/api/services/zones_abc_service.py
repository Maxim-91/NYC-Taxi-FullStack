import abc


class ZonesService(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()