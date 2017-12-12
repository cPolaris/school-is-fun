import time
import random
import numpy as np
import matplotlib.pyplot as plt
import iotools

# activation functions
def sigmoid(v):
    return 1.0 / (1.0 + np.exp(-v))


def relu(v):
    return max(0, v)

# initializers
def init_zeros(sz):
    return np.zeros(sz, dtype=float)


def init_uniform(sz, low=0.0, hi=1.0):
    return np.random.uniform(low=low, high=hi, size=sz)

# decay methods
# https://towardsdatascience.com/learning-rate-schedules-and-adaptive-learning-rate-methods-for-deep-learning-2c8f433990d1
def decay_time_based(lr, decay_rate, t):
    return lr * (1.0 / (1.0 + decay_rate * t))


def decay_exp(lr, k, t):
    return lr * np.exp(-k * t)


class Perceptron:
    """simplest perceptron: linear combination + activation"""

    def __init__(self, input_size, act, act_diffable, l_rate, initlzr, bias=True):
        """
        Parameters
        ----------
        input_size : int
            size of input vector (without bias term)
        act : function
            activation function e.g. sgn, sigmoid, relu, etc.
        act_diffable : bool
            is the activation function differentiable
        l_rate : float
            (initial) learning rate
        initlzr : function
            can initialize a vector of arbitrary size
        """
        self.w = initlzr(input_size + (1 if bias else 0))
        self.activation = act
        self.diffable = act_diffable
        self.l_rate = l_rate
        self.l_rate_0 = l_rate
        self.bias = bias

    def train(self, x, y):
        """
        Parameters
        ----------
        x : np array
            input vector
        y : int {1, -1}
            correct label
        """
        if (self.bias):
            x = np.hstack([x, 1])
        y_c = self.activation(np.dot(self.w, x))
        self.w += (y - y_c) * self.l_rate * x * (y_c * (1 - y_c) if self.diffable else 1.0)

    def predict(self, x):
        """
        Parameters
        ----------
        x : np array
            input vector
        Returns
        -------
        {1, -1} hot dog / not hot dog
        """
        return 1 if self.activation(np.dot(self.w, np.hstack([x, 1]) if self.bias else x)) > 0 else -1

    def train_epoch(self, xs, ys, epochs, decay=lambda x, t: x):
        """
        Parameters
        ----------
        train_x : np array
            training vectors
        train_y : np array
            training labels
        decay : function (lr, t) -> lr'
            decays the learning rate as a function of t
        epochs : int
            number of passes through the whole training set

        Returns
        -------
        {1, -1} hot dog / not hot dog
        """
        for ep in range(epochs):
            for ind, tx in enumerate(xs):
                self.train(tx, ys[ind])
            self.l_rate = decay(self.l_rate_0, ep+1)

    def train_epoch_random(self, xs, ys, epochs, decay=lambda x, t: x):
        for ep in range(epochs):
            # generate a random permutation
            indices = list(range(len(ys)))
            random.shuffle(indices)
            for ind in indices:
                self.train(xs[ind], ys[ind])
            self.l_rate = decay(self.l_rate_0, ep+1)


def multi_predict(ps, xs):
    """
    Predict the labels of some vector with a vector of perceptrons.

    Parameters
    ----------
    ps : list
        list of perceptrons

    xs : np 2d array
        input vectors

    Returns
    -------
    labels: [0, len(ps)-1]
    """
    return np.fromiter((np.argmax([p.predict(x) for p in ps]) for x in xs), dtype=int)


def main():
    CLASSES = 10
    EPOCHS = 5
    WITH_BIAS = True
    DECAY_RATE = 1.0
    train_xs, train_ys, test_xs, test_ys = iotools.read_digitdata()

    perceptrons = []
    print('start training')
    start_time = time.time()
    for cls in range(10):
        p = Perceptron(784, np.sign, False, 1.0, init_zeros, bias=True)
        p.train_epoch(train_xs, np.fromiter((1 if ty == cls else -1 for ty in train_ys), dtype=int), EPOCHS, decay=lambda x,t:decay_time_based(x,DECAY_RATE,t))
        perceptrons.append(p)
    print('training took %f sec' % (time.time() - start_time))
    pred_labels = multi_predict(perceptrons, test_xs)
    accuracy = np.sum(pred_labels == test_ys) / len(test_ys)
    print('Accuracy', accuracy)

    print('bincount test_y', np.bincount(test_ys))
    print('bincount prediction', np.bincount(pred_labels))

    # confusion matrix
    confusions = np.zeros((10,10))
    for row in range(10):
        row_total = np.sum(test_ys == row)
        for col in range(10):
            confusions[row, col] = np.sum(pred_labels[test_ys == row] == col) / row_total
    plt.figure()
    plt.suptitle('confusion matrix')
    plt.imshow(confusions, cmap='rainbow')
    plt.colorbar()

    # perceptron weights
    for ind, percep in enumerate(perceptrons):
        plt.figure()
        plt.title(ind)
        plt.imshow(np.reshape(percep.w[:784], (28,28)), cmap='gray')
    plt.show()

if __name__ == '__main__':
    main()
