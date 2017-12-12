import iotools
import time
import numpy as np
from sklearn import svm, tree
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import AdaBoostClassifier

train_xs, train_ys, test_xs, test_ys = iotools.read_digitdata()

# http://scikit-learn.org/stable/modules/svm.html#multi-class-classification
# svm
print('SVM one-vs-rest')
classifier = svm.LinearSVC()

print('start training')
start_time = time.time()
classifier.fit(train_xs, train_ys)
print('training took %f sec' % (time.time() - start_time,))

decisions = classifier.decision_function(test_xs)
dec_labels = np.fromiter((np.argmax(dec) for dec in decisions), dtype=int)
accuracy = np.sum(dec_labels == test_ys) / len(test_ys)
print('accuracy: ', accuracy)

print('------------------------------------------')

print('SVM one-vs-one')
classifier = svm.SVC(decision_function_shape='ovr')

print('start training')
start_time = time.time()
classifier.fit(train_xs, train_ys)
print('training took %f sec' % (time.time() - start_time,))

decisions = classifier.decision_function(test_xs)
dec_labels = np.fromiter((np.argmax(dec) for dec in decisions), dtype=int)
accuracy = np.sum(dec_labels == test_ys) / len(test_ys)
print('accuracy: ', accuracy)

print('------------------------------------------')

# http://scikit-learn.org/stable/modules/tree.html#classification
print('Decision Tree')
classifier = tree.DecisionTreeClassifier()

print('start training')
start_time = time.time()
classifier.fit(train_xs, train_ys)
print('training took %f sec' % (time.time() - start_time,))

dec_labels = classifier.predict(test_xs)
accuracy = np.sum(dec_labels == test_ys) / len(test_ys)
print('accuracy: ', accuracy)

print('------------------------------------------')

# http://scikit-learn.org/stable/modules/tree.html#classification
print('Logistic Regression one-vs-rest liblinear')
classifier = LogisticRegression(solver='liblinear').fit(train_xs, train_ys)

print('start training')
start_time = time.time()
classifier.fit(train_xs, train_ys)
print('training took %f sec' % (time.time() - start_time,))

dec_labels = classifier.predict(test_xs)
accuracy = np.sum(dec_labels == test_ys) / len(test_ys)
print('accuracy: ', accuracy)

# print('------------------------------------------')

# http://scikit-learn.org/stable/modules/ensemble.html#adaboost
# print('Ensemble: Adaboost-SAMME decision tree')
# classifier = AdaBoostClassifier(n_estimators=250, learning_rate=1.0)

# print('start training')
# start_time = time.time()
# classifier.fit(train_xs, train_ys)
# print('training took %f sec' % (time.time() - start_time,))

# dec_labels = classifier.predict(test_xs)
# accuracy = np.sum(dec_labels == test_ys) / len(test_ys)
# print('accuracy: ', accuracy)
