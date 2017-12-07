import random
import iotools
import numpy as np
import matplotlib.pyplot as plt


def main():
    train_xs, train_ys, test_xs, test_ys = iotools.read_yesno_data()
    n_train, n_feat = train_xs.shape
    n_test = test_ys.shape[0]
    counts = np.bincount(train_ys)
    priors = counts / train_ys.shape[0]

    # training with binary feature
    k = 0.1  # smoothing
    # 10 classes, 2 values per feature
    # [cls, feat, 0] = likelihood of feat is False given cls
    likelihoods = np.zeros((10, n_feat, 2))

    for cls in range(10):
        likelihoods[cls, :, 1] = (np.sum(train_xs[train_ys == cls], axis=0) + k) / (k * 2.0 + counts[cls])
        likelihoods[cls, :, 0] = (np.sum(0 == train_xs[train_ys == cls], axis=0) + k) / (k * 2.0 + counts[cls])

    # sanity check
    # print(likelihoods[3,389,1])
    # print((np.sum(train_xs[train_ys == 3][:,389]) + k) / (k * 2.0 + counts[3]))

    # testing
    log_likelihoods = np.log(likelihoods)
    log_priors = np.log(priors)
    posteriors = np.zeros((n_test, 10))  # posterior probability for each testing image of each class
    for test_ind, test_row in enumerate(test_xs):
        for cls in range(10):
            posteriors[test_ind, cls] += log_priors[cls]
            for feat_ind in range(n_feat):
                posteriors[test_ind, cls] += log_likelihoods[cls, feat_ind, 1 if test_row[feat_ind] else 0]

    # evaluation
    predictions = np.argmax(posteriors, axis=1)

    # for each class, draw the example with highest and lowest posterior
    plt.figure()
    plt.title('most and least prototypical')
    for cls in range(10):
        cls_posts = posteriors[predictions == cls, cls]
        most = np.argmax(cls_posts)
        least = np.argmin(cls_posts)
        plt.subplot(2,10,cls+1).set_title(cls)
        plt.imshow(test_xs[most].reshape(28,28), cmap='gray')
        plt.subplot(2,10,cls+11).set_title(cls)
        plt.imshow(test_xs[least].reshape(28,28), cmap='gray')
    plt.show()

    # draw some predictions
    plt.figure()
    plt.title('some prediction results')
    for j, ind in enumerate(random.sample(range(n_test), 10)):
        fig = plt.subplot(2,5,j+1)
        fig.set_title(test_ys[ind])
        fig.axis('off')
        plt.imshow(test_xs[ind].reshape((28,28)), cmap='gray')
    plt.show()

    total_correct = 0
    total_count = 0

    print('digit\taccuracy')
    for cls in range(10):
        cls_correct = np.sum(predictions[test_ys == cls] == cls)
        total_correct += cls_correct
        cls_total = np.sum(test_ys == cls)
        total_count += cls_total
        cls_acc = cls_correct / cls_total
        print('%d\t%f' % (cls, cls_acc))
    print('Overall accuracy: ', total_correct / total_count)

    # confusion matrix
    # m[r,c] = percentage of r classified as c
    confusions = np.zeros((10,10))
    for row in range(10):
        for col in range(10):
            confusions[row, col] = np.sum(predictions[test_ys == row] == col) / np.sum(test_ys == row)
    plt.figure()
    plt.title('confusion matrix')
    plt.imshow(confusions, cmap='gray')

    # odds ratios


    plt.show()

if __name__ == '__main__':
    main()
