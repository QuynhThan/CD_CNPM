"""
Compress/Decompress terms in a dictionary
"""
from collections import OrderedDict


# /*
#  *
#  * N19DCCN140-Trần Tấn Phong
#  * N19DCCN147-Lê Nguyễn Duy Phương
#  * N19DCCN154- Thân Ngọc Quỳnh
#  */

class CompressedDict:
    def __init__(self):
        self.termsString = ""
        self.ptrString = list([0])
        self.ptrPostingLst = list()

    def insert(self, term, ptrPosting):
        self.ptrString.append(len(term) + len(self.termsString))
        self.termsString += term + str(len(term))  # Add term to string

        self.ptrPostingLst.append(ptrPosting)  # Add term pointer


if __name__ == "__main__":
    vocab_list = []  # [{id: 1, vocab: abc}]
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
    compressDict = CompressedDict()
    for vocab_obj in vocab_list:
        compressDict.insert(vocab_obj["vocab"], int(vocab_obj["id"]))

    result_file = open("out-long-string.txt", "w")
    result_file.write(compressDict.termsString)
    # result_file.write("\n")
    # result_file.write(" ".join(str(x) for x in compressDict.ptrString))
    # result_file.write("\n")
    # result_file.write(" ".join(str(x) for x in compressDict.ptrPostingLst))
    result_file.close()
