import time
import numpy as np
from iotools import read_digitdata
import copy as cp
import matplotlib.pyplot as plt
import operator

def n2_distance(a,b):
	return np.linalg.norm(a-b)

def n1_distance(a,b):
	return np.sum(np.abs(a-b))

def cosine_distance(a,b):
	return spatial.distance.cosine(a,b)

def most_frequent_element(li):
	return max(set(li),key=li.count)

def knn(k,train_data,test_xs_instance):

	dis=[]
	for i in train_data:
		d=n2_distance(i[0:-1],test_xs_instance)
		dis.append((i,d))
	dis.sort(key=operator.itemgetter(1))
	close_neightbor=[]
	for j in range(k):
		close_neightbor.append(dis[j][0][-1])
	return most_frequent_element(close_neightbor)

def main():
	k=1
	train_xs, train_ys, test_xs, test_ys = read_digitdata()
	train_xs=train_xs.astype(int)
	train_ys=train_ys.astype(int)
	test_xs=test_xs.astype(int)
	test_ys=test_ys.astype(int)
	train_data=np.concatenate((train_xs,train_ys.reshape(len(train_ys),1)),axis=1)
	counter=0
	num_data=test_xs.shape[0]
	pred_labels = []
	for index,i in enumerate(test_xs):
		label=knn(k,train_data,i)
		pred_labels.append(label)
	pred_labels = np.fromiter(pred_labels, dtype=int)
	accuracy = np.sum(pred_labels == test_ys) / len(test_ys)
	print('Accuracy', accuracy)
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
	plt.show()

if __name__ == '__main__':
    main()
