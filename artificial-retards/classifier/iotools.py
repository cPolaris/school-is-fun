import os
import random
import numpy as np
import matplotlib.pyplot as plt

NUM_TRAIN = 5000
NUM_TEST = 1000
CHAR_VAL = {
    ' ': 0.0,
    '+': 0.5,
    '#': 1.0
}
CHAR_VAL_BIN = {
    ' ': False,
    '+': True,
    '#': True
}
HI_ENRG = ' '
LO_ENRG = '%'
PIXEL_DTYPE = np.float32
DIGIT_LABEL_DTYPE = np.uint8

def read_digitdata(data_dir='digitdata', show_pic=False, binary_feat=True):
    """
    reads data, returns numpy array representation
    returns: (train_xs, train_ys, test_xs, test_ys)
    train_xs (5000, 784) float32 or bool (if binary_feat true)
    train_ys (5000,) int, from 0 to 9 inclusive
    test_xs  (1000, 784) float32 or bool
    test_ys  (1000,) int, from 0 to 9 inclusive
    """
    if binary_feat:
        train_xs = np.zeros((5000, 784), dtype=bool)
        test_xs = np.zeros((1000, 784), dtype=bool)
    else:
        train_xs = np.zeros((5000, 784), dtype=PIXEL_DTYPE)
        test_xs = np.zeros((1000, 784), dtype=PIXEL_DTYPE)
    train_ys = np.zeros(5000, dtype=DIGIT_LABEL_DTYPE)
    test_ys = np.zeros(1000, dtype=DIGIT_LABEL_DTYPE)

    def read_images(file, out_vec):
        with open(file, 'r', encoding='ascii') as in_file:
            for row, line in enumerate(in_file):
                for col, ch in enumerate(line.rstrip('\n')):
                    arr_row = row * 28 // 784
                    arr_col = row * 28 % 784 + col
                    out_vec[arr_row, arr_col] = CHAR_VAL_BIN[ch] if binary_feat else CHAR_VAL[ch]

    def read_labels(file, out_vec):
        with open(file, 'r', encoding='ascii') as in_file:
            for row, line in enumerate(in_file):
                out_vec[row] = line[0]

    read_images(os.path.join(data_dir, 'trainingimages'), train_xs)
    read_labels(os.path.join(data_dir, 'traininglabels'), train_ys)
    read_images(os.path.join(data_dir, 'testimages'), test_xs)
    read_labels(os.path.join(data_dir, 'testlabels'), test_ys)
    if show_pic:
        chosen_ind = random.randint(0, 5000)
        chosen_pic = train_xs[chosen_ind].reshape((28, 28))
        print(chosen_pic)
        plt.imshow(chosen_pic)
        plt.title(train_ys[chosen_ind])
        plt.show()
    return (train_xs, train_ys, test_xs, test_ys)

def read_face_data(data_dir='facedata', show_pic=False):
    """
    reads data, returns numpy array representation
    images are 70 rows * 60 cols
    returns: (train_xs, train_ys, test_xs, test_ys)
    train_xs (451, 4200) float32
    train_ys (451,) bool
    test_xs  (150, 4200) float32
    test_ys  (150,) bool
    """
    train_xs = np.zeros((451, 4200), dtype=np.float32)
    train_ys = np.zeros(451, dtype=np.bool)
    test_xs = np.zeros((150, 4200), dtype=np.float32)
    test_ys = np.zeros(150, dtype=np.bool)

    def read_images(file, out_vec):
        with open(file, 'r', encoding='ascii') as in_file:
            for row, line in enumerate(in_file):
                for col, ch in enumerate(line.rstrip('\n')):
                    arr_row = row * 60 // 4200
                    arr_col = row * 60 % 4200 + col
                    out_vec[arr_row, arr_col] = CHAR_VAL[ch]

    def read_labels(file, out_vec):
        with open(file, 'r', encoding='ascii') as in_file:
            for row, line in enumerate(in_file):
                out_vec[row] = line[0] == '1'

    read_images(os.path.join(data_dir, 'facedatatrain'), train_xs)
    read_labels(os.path.join(data_dir, 'facedatatrainlabels'), train_ys)
    read_images(os.path.join(data_dir, 'facedatatest'), test_xs)
    read_labels(os.path.join(data_dir, 'facedatatestlabels'), test_ys)
    if show_pic:
        for _ in range(3):
            chosen_ind = random.randint(0, 451)
            print(chosen_ind)
            plt.imshow(train_xs[chosen_ind].reshape((70, 60)))
            plt.title(train_ys[chosen_ind])
            plt.show()
    return (train_xs, train_ys, test_xs, test_ys)

def read_yesno_data(data_dir='yesno', show_pic=False):
    """
    reads data, returns numpy array representation
    spectrograms are 25 rows * 10 cols
    separated by 3 blank lines
    returns: (train_xs, train_ys, test_xs, test_ys)
    no_test (50, 250)
    yes_test (50, 250)
    no_train (131, 250)
    yes_train (140, 250)
    """
    def read_spect(file):
        pass
    pass

def main():
    # read_digitdata(show_pic=True)
    read_face_data(show_pic=True)


if __name__ == '__main__':
    main()
