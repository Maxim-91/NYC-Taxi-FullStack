import abc


class SzRepository(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()