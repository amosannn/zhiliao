package com.amosannn.service;

import com.amosannn.model.Collection;
import java.util.List;
import java.util.Map;

public interface CollectionService {

  Map<String, Object> getCollectionContent(Integer collectionId, Integer userId);

  List<Collection> listCreatingCollection(Integer userId);

}
