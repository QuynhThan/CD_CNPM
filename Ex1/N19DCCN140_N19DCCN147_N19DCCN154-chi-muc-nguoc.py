import nltk

# /*
#  *
#  * N19DCCN140-Trần Tấn Phong
#  * N19DCCN147-Lê Nguyễn Duy Phương
#  * N19DCCN154- Thân Ngọc Quỳnh
#  */

vocab_obj = {}  # chứa danh sách các từ vựng
# Doc List tu vung
f_vocab = open('./npl/term-vocab', 'r')

for x in f_vocab:
    try:
        vocab = f"{x.lower().strip().split(' ')[1]}"

        vocab_obj[vocab] = []
    except IndexError:
        break

# print(vocab_obj)

# doc file doc để coi là list từ vựng xuất hiện trong những doc nào
doc = {
    'id': 1,
    'content': '',
}
list_docs = []
f_doc = open('./npl/doc-text', 'r')

for x in f_doc:
    # x = x.strip()[:-1]
    # x = x.strip()[:-1]
    if x.strip().isnumeric():
        doc['id'] = int(x)
        continue
    elif x.strip() == '/':
        list_docs.append(doc)
        doc = {'id': 1, 'content': ''}
    else:
        x = x[:-1]  # xoa '\n' cuoi dong
        doc['content'] += x

    # if (x.isnumeric()):
    #     list_test.append(x)

f_doc.close()

# kiem tra xe tu vung xuat hien trong nhung doc nao
for vocab in vocab_obj.keys():
    for doc in list_docs:
        if vocab in doc['content']:
            vocab_obj[vocab].append(doc['id'])


# load file truy van
query = {
    'id': 1,
    'content': '',
}
list_queries = []
f_query = open('./npl/query-text', 'r')

for x in f_query:
    # x = x.strip()[:-1]
    # x = x.strip()[:-1]
    if x.strip().isnumeric():
        query['id'] = int(x)
        continue
    elif x.strip() == '/':
        list_queries.append(query)
        query = {'id': 1, 'content': ''}
    else:
        x = x[:-1]  # xoa '\n' cuoi dong
        query['content'] += x.lower()

    # if (x.isnumeric()):
    #     list_test.append(x)

f_query.close()


# lặp qua từng query và bỏ đi giới từ + mạo từ 'a,an, the', và thêm vào một thuộc tính là kết quả (gồm những doc mà có xuất hiện query )
# fisrt_query_obj = list_queries[92]
for query_obj in list_queries:
    tokens = nltk.word_tokenize(query_obj['content'])
    tagged = nltk.pos_tag(tokens)
    content_with_no_preps_and_the_list = [
        item[0] for item in tagged if item[1] != 'IN' and item[1] != 'DT' and item[1] != 'CC']
    query_obj['content'] = ' '.join(content_with_no_preps_and_the_list)

# giờ tách query ra thành từng từ vựng -> từng từ vựng ta sẽ co list các đoc mà xuất hiện từ vựng ấy -> lấy giao lại rồi
# query_obj = {id: number, content: string, answer: []}
# list_queries = [query_obj]
for query in list_queries:
    vocab_list_from_query = []
    for vocab_query in query['content'].split(' '):
        print(vocab_query)
        temp_list = []
        for vocab in vocab_obj.keys():
            if vocab_query.startswith(vocab):
                temp_list.append(vocab)
        if (len(temp_list) == 0):
            break
            # vocab_list_from_query.append(temp_list[index])

        index = 0
        length = 0

        for i, vocab in enumerate(temp_list):
            if (len(vocab) > length):
                length = len(vocab)
                index = i
                # print(f'index: {index}, vocab: {vocab}')
        vocab_list_from_query.append(temp_list[index])
        # print(vocab_list_from_query)
    # print(set(vocab_obj[vocab_list_from_query[0]]).intersection(set(vocab_obj[vocab_list_from_query[1]])))
    # break;
    if (len(vocab_list_from_query) == 0):
        print(query)
        break
    answer = set(vocab_obj[vocab_list_from_query[0]])
    # print(f'vocab: {vocab_list_from_query[0]}, answer: {answer}')

    for vocab in vocab_list_from_query:
        vocab_set = set(vocab_obj[vocab])
        answer = answer.intersection(vocab_set)
        # print(f'answer: {answer}')
    query['answer'] = answer
    # print(query)

# print(list_queries)
# print(list_queries[0])
# for vocab in vocab_obj.keys():
#     print(vocab)
# print(list_queries[0])
print(list_queries)
