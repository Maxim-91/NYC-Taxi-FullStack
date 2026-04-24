import abc


class ZonesRepository(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()