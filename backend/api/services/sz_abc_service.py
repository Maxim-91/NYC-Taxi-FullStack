import abc


class SzService(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()