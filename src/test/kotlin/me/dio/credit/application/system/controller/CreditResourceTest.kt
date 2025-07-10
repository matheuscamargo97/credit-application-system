package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
  @Autowired private lateinit var creditRepository: CreditRepository
  @Autowired  private lateinit var customerRepository: CustomerRepository

  @Autowired private lateinit var mockMvc: MockMvc

  @Autowired private lateinit var objectMapper: ObjectMapper

  companion object {
    const val URL: String = "/api/credits"
  }

  @BeforeEach
  fun setup() = creditRepository.deleteAll()

  @AfterEach
  fun tearDown() = creditRepository.deleteAll()

  @Test
  fun `should save credit`() {
    //given
    val creditDto: CreditDto = buildCreditDto()
    customerRepository.save(buildCustomer())
    val valueAsString: String = objectMapper.writeValueAsString(creditDto)
    //when
    //then
    mockMvc.perform(
      MockMvcRequestBuilders.post(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsString)
    )
      .andExpect(MockMvcResultMatchers.status().isCreated)
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  fun `should not save credit`() {
    //given
    val creditDto: CreditDto = buildInvalidCreditDto()
    val valueAsString: String = objectMapper.writeValueAsString(creditDto)
    //when
    //then
    mockMvc.perform(
      MockMvcRequestBuilders.post(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsString)
    )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  fun `should return list of credits by customerId`() {
    // given
    val creditDto: CreditDto = buildCreditDto()
    customerRepository.save(buildCustomer())
    creditRepository.save(buildCredit())
    creditRepository.save(buildCredit())
    creditRepository.save(buildCredit())
    val customerId: Long = 1L

    // when/then
    mockMvc.perform(
      MockMvcRequestBuilders.get(URL)
        .param("customerId", customerId.toString()) // <-- Aqui define o request param
        .contentType(MediaType.APPLICATION_JSON)
    )
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
      .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditCode").isNotEmpty)
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditValue").value(100.00))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].numberOfInstallments").value(15))
      .andExpect(MockMvcResultMatchers.jsonPath("$[1].creditCode").isNotEmpty)
      .andExpect(MockMvcResultMatchers.jsonPath("$[1].creditValue").value(100.00))
      .andExpect(MockMvcResultMatchers.jsonPath("$[1].numberOfInstallments").value(15))      .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditCode").isNotEmpty)
      .andExpect(MockMvcResultMatchers.jsonPath("$[2].creditCode").isNotEmpty)
      .andExpect(MockMvcResultMatchers.jsonPath("$[2].creditValue").value(100.00))
      .andExpect(MockMvcResultMatchers.jsonPath("$[2].numberOfInstallments").value(15))
      .andDo(MockMvcResultHandlers.print())
  }

  fun buildCustomer(
    firstName: String = "Cami",
    lastName: String = "Cavalcante",
    cpf: String = "28475934625",
    email: String = "camila@gmail.com",
    password: String = "12345",
    zipCode: String = "12345",
    street: String = "Rua da Cami",
    income: BigDecimal = BigDecimal.valueOf(1000.0),
    id: Long = 1L
  ) = Customer(
    firstName = firstName,
    lastName = lastName,
    cpf = cpf,
    email = email,
    password = password,
    address = Address(
      zipCode = zipCode,
      street = street,
    ),
    income = income,
    id = id
  )

  private fun buildCreditDto(
    creditValue: BigDecimal = BigDecimal.valueOf(100.0),
    dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
    numberOfInstallments: Int = 15,
    customer: Customer = buildCustomer()
  ): CreditDto = CreditDto(
    creditValue = creditValue,
    dayFirstOfInstallment = dayFirstInstallment,
    numberOfInstallments = numberOfInstallments,
    customerId = customer.id!!
  )

  private fun buildInvalidCreditDto(
    creditValue: BigDecimal = BigDecimal.valueOf(100.0),
    dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
    numberOfInstallments: Int = 15,
    customer: Customer = buildCustomer()
  ): CreditDto = CreditDto(
    creditValue = creditValue,
    dayFirstOfInstallment = dayFirstInstallment,
    numberOfInstallments = numberOfInstallments,
    customerId = 99
  )

  private fun buildCredit(
    creditValue: BigDecimal = BigDecimal.valueOf(100.0),
    dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
    numberOfInstallments: Int = 15,
    customer: Customer = buildCustomer()
  ): Credit = Credit(
    creditValue = creditValue,
    dayFirstInstallment = dayFirstInstallment,
    numberOfInstallments = numberOfInstallments,
    customer = customer
  )
}