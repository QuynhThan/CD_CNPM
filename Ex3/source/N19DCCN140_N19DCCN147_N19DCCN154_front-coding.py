import sys

# /*
#  *
#  * N19DCCN140-Trần Tấn Phong
#  * N19DCCN147-Lê Nguyễn Duy Phương
#  * N19DCCN154- Thân Ngọc Quỳnh
#  */


def front_coding(postings_list):
    compressed_postings_list = []
    prefix = ""

    for i, posting in enumerate(postings_list):
        if i == 0:
            compressed_postings_list.append(posting)
            prefix = posting
            continue

        common_prefix_len = 0
        for j in range(min(len(prefix), len(posting))):
            if prefix[j] != posting[j]:
                break
            common_prefix_len += 1

        if common_prefix_len == 0:
            compressed_postings_list.append(posting)
        else:
            compressed_postings_list.append(
                str(common_prefix_len) + posting[common_prefix_len:]
            )

        prefix = posting

    return compressed_postings_list


postings_list = [
    "apple",
    "animal",
    "apricot",
    "apartment",
    "application",
    "banana",
    "bandana",
    "cabbage",
    "caboose",
    "cabin",
]
compressed_postings_list = front_coding(postings_list)
print(compressed_postings_list)


# doc file tu vung

vocab_list = []
vocab_obj = {}  # chứa danh sách các từ vựng
# Doc List tu vung
f_vocab = open("./npl/term-vocab", "r")

for x in f_vocab:
    try:
        if len(x.lower().strip().split(" ")) == 3:
            vocab_id, vocab, _ = x.lower().strip().split(" ")
            vocab_list.append({"id": vocab_id, "vocab": vocab})
    except IndexError:
        break

f_vocab.close()

# sort tang dan theo tu vung
vocab_list.sort(key=lambda x: x["vocab"])

only_vocab_id = [x["id"] for x in vocab_list]
only_vocab_value = [x["vocab"] for x in vocab_list]

# apply front-coding
compressed_vocab_list = front_coding(only_vocab_value)
# print(compressed_vocab_list)
test = [x for x in compressed_vocab_list if x.isalpha()]
print(test)

# lưu ra file la danh sach tu vung thu tu a-z

result_file = open("out-vocab-front-coding.txt", "w")
for index, vocab in enumerate(compressed_vocab_list):
    id = only_vocab_id[index]
    result_file.writelines(f"{id} {vocab}")
    result_file.write("\n")

result_file.close()
