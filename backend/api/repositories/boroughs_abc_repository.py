import abc


class BoroughsRepository(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()