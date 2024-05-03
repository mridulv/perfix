package io.perfix.common

import io.perfix.manager.DatasetManager
import io.perfix.model.{DatasetId, DatasetParams}
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.util.Random

class DatasetManagerTest extends AnyFlatSpec with MockitoSugar {

  "DatasetManager" should "create a new dataset with unique ID" in {
    val datasetManager = new DatasetManager(new InMemoryDatasetConfigStore)

    val datasetParams = DatasetParams(id = None, name = s"dataset-${Random.nextInt()}", rows = 100, columns = Seq.empty)
    val datasetId = datasetManager.create(datasetParams)

    datasetManager.get(datasetId).dataset.params.copy(id = None) shouldBe datasetParams
  }

  "DatasetManager" should "retrieve a dataset by ID" in {
    val datasetManager = new DatasetManager(new InMemoryDatasetConfigStore)
    val datasetParams = DatasetParams(id = None, name = s"dataset-${Random.nextInt()}", rows = 100, columns = Seq.empty)
    val datasetId = datasetManager.create(datasetParams)

    val retrievedDataset = datasetManager.get(datasetId)
    val expectedDataset = datasetParams.dataset.sampleDataset(100)

    retrievedDataset.dataset.params.copy(id = None) shouldBe datasetParams
  }

  "DatasetManager" should "retrieve all datasets" in {
    val datasetManager = new DatasetManager(new InMemoryDatasetConfigStore)
    val datasetParams1 = DatasetParams(id = None, name = s"dataset-${Random.nextInt()}", rows = 100, columns = Seq.empty)
    val datasetParams2 = DatasetParams(id = None, name = s"dataset-${Random.nextInt()}", rows = 200, columns = Seq.empty)

    datasetManager.create(datasetParams1)
    datasetManager.create(datasetParams2)

    val allDatasets = datasetManager.all().map(e => e.copy(id = None))

    allDatasets.size shouldBe 2
    allDatasets.contains(datasetParams1) shouldBe true
    allDatasets.contains(datasetParams2) shouldBe true
  }

}

